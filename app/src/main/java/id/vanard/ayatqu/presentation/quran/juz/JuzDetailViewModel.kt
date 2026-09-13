package id.vanard.ayatqu.presentation.quran.juz

import android.app.Application
import androidx.lifecycle.viewModelScope
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import id.vanard.ayatqu.R
import id.vanard.ayatqu.data.local.AyahAudioCache
import id.vanard.ayatqu.domain.model.JuzVerse
import id.vanard.ayatqu.domain.model.LastRead
import id.vanard.ayatqu.domain.repository.QuranRepository
import id.vanard.ayatqu.presentation.common.viewmodel.BaseMviViewModel
import id.vanard.ayatqu.presentation.quran.juz.contract.JuzDetailEvent
import id.vanard.ayatqu.presentation.quran.juz.contract.JuzDetailSideEffect
import id.vanard.ayatqu.presentation.quran.juz.contract.JuzDetailState
import id.vanard.ayatqu.service.PlaybackForegroundService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class JuzDetailViewModel(
    private val application: Application,
    private val repository: QuranRepository,
    private val audioCache: AyahAudioCache,
) : BaseMviViewModel<JuzDetailState, JuzDetailEvent, JuzDetailSideEffect>(JuzDetailState()) {

    private val exoPlayer = ExoPlayer.Builder(application)
        .setAudioAttributes(AudioAttributes.DEFAULT, true)
        .setHandleAudioBecomingNoisy(true)
        .setWakeMode(C.WAKE_MODE_NETWORK)
        .build()
    private val mediaSession = MediaSession.Builder(application, exoPlayer).build()

    init {
        viewModelScope.launch {
            repository.getLastRead().collect { lastRead ->
                setState { copy(currentLastRead = lastRead) }
            }
        }
        exoPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_BUFFERING -> setState { copy(isPreparingAudio = true) }
                    Player.STATE_READY -> setState { copy(isPreparingAudio = false) }
                    Player.STATE_ENDED -> {
                        setState { copy(isPreparingAudio = false) }
                        playNextAyah()
                    }
                }
            }
        })
    }

    override fun onEvent(event: JuzDetailEvent) {
        when (event) {
            is JuzDetailEvent.InitialData -> loadJuz(event.juzNumber.coerceIn(1, 30))
            is JuzDetailEvent.VerseClicked -> {
                if (uiState.value.playingVerseKey != null) stopPlayback()
                setEffect(JuzDetailSideEffect.NavigateToVerse(event.surahNumber, event.ayahNumber))
            }
            is JuzDetailEvent.PlayAyahClicked -> playAyah(event.surahNumber, event.ayahNumber)
            is JuzDetailEvent.DownloadAyahClicked -> downloadAyah(event.surahNumber, event.ayahNumber)
            is JuzDetailEvent.SetLastReadClicked -> setLastRead(event.surahNumber, event.ayahNumber)
            JuzDetailEvent.DownloadAllClicked -> downloadAll()
            JuzDetailEvent.StopPlaybackClicked -> stopPlayback()
            JuzDetailEvent.ConfirmOverwriteClicked -> confirmOverwrite()
            JuzDetailEvent.DismissOverwriteClicked -> dismissOverwriteDialog()
            JuzDetailEvent.ErrorConsumed -> setState { copy(errorMessage = null) }
            JuzDetailEvent.RetryClicked -> loadJuz(uiState.value.juzNumber)
            JuzDetailEvent.BackClicked -> setEffect(JuzDetailSideEffect.NavigateBack)
        }
    }

    private fun loadJuz(juzNumber: Int) {
        if (uiState.value.juz?.number == juzNumber) return
        viewModelScope.launch {
            setState {
                copy(juzNumber = juzNumber, isLoading = true, juz = null, errorMessage = null)
            }
            repository.getJuz(juzNumber)
                .onSuccess { juz ->
                    val downloaded = juz.verses
                        .filter { audioCache.isDownloaded(it.surahNumber, it.ayahNumber) }
                        .mapTo(mutableSetOf(), JuzVerse::verseKey)
                    setState {
                        copy(
                            isLoading = false,
                            juz = juz,
                            downloadedVerseKeys = downloaded,
                        )
                    }
                }
                .onFailure { error ->
                    val message = error.message ?: application.getString(R.string.couldnt_load_juz)
                    setState { copy(isLoading = false, errorMessage = message) }
                }
        }
    }

    private fun playAyah(surahNumber: Int, ayahNumber: Int) {
        val verse = findVerse(surahNumber, ayahNumber) ?: return
        val state = uiState.value
        if (verse.verseKey !in state.downloadedVerseKeys) return

        if (state.playingVerseKey == verse.verseKey) {
            exoPlayer.pause()
            setState { copy(playingVerseKey = null, isPreparingAudio = false) }
            PlaybackForegroundService.stop(application)
            return
        }

        val localPath = audioCache.getLocalPath(surahNumber, ayahNumber) ?: return
        exoPlayer.stop()
        exoPlayer.setMediaItem(
            MediaItem.Builder()
                .setUri("file://$localPath")
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle("${verse.surahName} - Ayah ${verse.ayahNumber}")
                        .setArtist("Quran")
                        .build()
                )
                .build()
        )
        exoPlayer.prepare()
        exoPlayer.play()
        setState { copy(playingVerseKey = verse.verseKey) }
        PlaybackForegroundService.start(
            context = application,
            title = verse.surahName,
            text = "Playing Ayah ${verse.ayahNumber}",
        )
    }

    private fun findVerse(surahNumber: Int, ayahNumber: Int): JuzVerse? =
        uiState.value.juz?.verses?.find {
            it.surahNumber == surahNumber && it.ayahNumber == ayahNumber
        }

    private fun playNextAyah() {
        val state = uiState.value
        val currentKey = state.playingVerseKey ?: return
        val verses = state.juz?.verses.orEmpty()
        val currentIndex = verses.indexOfFirst { it.verseKey == currentKey }
        val next = verses.getOrNull(currentIndex + 1)
        if (next != null && next.verseKey in state.downloadedVerseKeys) {
            playAyah(next.surahNumber, next.ayahNumber)
        } else {
            stopPlayback()
        }
    }

    private fun stopPlayback() {
        exoPlayer.stop()
        setState { copy(playingVerseKey = null, isPreparingAudio = false) }
        PlaybackForegroundService.stop(application)
    }

    private fun downloadAyah(surahNumber: Int, ayahNumber: Int) {
        val verse = findVerse(surahNumber, ayahNumber) ?: return
        val state = uiState.value
        if (verse.verseKey in state.downloadingVerseKeys ||
            verse.verseKey in state.downloadedVerseKeys
        ) return

        setState { copy(downloadingVerseKeys = downloadingVerseKeys + verse.verseKey) }
        viewModelScope.launch {
            try {
                downloadVerse(verse)
                setState {
                    copy(
                        downloadedVerseKeys = downloadedVerseKeys + verse.verseKey,
                        downloadingVerseKeys = downloadingVerseKeys - verse.verseKey,
                    )
                }
            } catch (error: Exception) {
                val message = application.getString(
                    R.string.ayah_download_failed,
                    verse.verseKey,
                    error.message.orEmpty(),
                )
                setState {
                    copy(
                        downloadingVerseKeys = downloadingVerseKeys - verse.verseKey,
                        errorMessage = message,
                    )
                }
                setEffect(JuzDetailSideEffect.ShowMessage(message))
            }
        }
    }

    private fun downloadAll() {
        val state = uiState.value
        if (state.isDownloadingAll) return
        val verses = state.juz?.verses.orEmpty().filter {
            it.verseKey !in state.downloadedVerseKeys &&
                it.verseKey !in state.downloadingVerseKeys
        }
        if (verses.isEmpty()) return

        val keys = verses.mapTo(mutableSetOf(), JuzVerse::verseKey)
        setState {
            copy(
                isDownloadingAll = true,
                downloadingVerseKeys = downloadingVerseKeys + keys,
                downloadProgress = 0 to verses.size,
            )
        }

        viewModelScope.launch {
            var failures = 0
            verses.forEachIndexed { index, verse ->
                try {
                    downloadVerse(verse)
                    setState {
                        copy(downloadedVerseKeys = downloadedVerseKeys + verse.verseKey)
                    }
                } catch (_: Exception) {
                    failures++
                } finally {
                    setState {
                        copy(
                            downloadingVerseKeys = downloadingVerseKeys - verse.verseKey,
                            downloadProgress = (index + 1) to verses.size,
                        )
                    }
                }
            }
            setState { copy(isDownloadingAll = false, downloadProgress = null) }
            if (failures > 0) {
                setEffect(
                    JuzDetailSideEffect.ShowMessage(
                        application.getString(R.string.audio_download_failures, failures)
                    )
                )
            }
        }
    }

    private suspend fun downloadVerse(verse: JuzVerse) = withContext(Dispatchers.IO) {
        val audioUrl = repository
            .getAyahAudioUrl(verse.surahNumber, verse.ayahNumber)
            .getOrThrow()
        val file = audioCache.getAyahFile(verse.surahNumber, verse.ayahNumber)
        URL(audioUrl).openStream().use { input ->
            file.outputStream().use { output -> input.copyTo(output) }
        }
        audioCache.markDownloaded(verse.surahNumber, verse.ayahNumber, file.absolutePath)
    }

    private fun setLastRead(surahNumber: Int, ayahNumber: Int) {
        val verse = findVerse(surahNumber, ayahNumber) ?: return
        val existing = uiState.value.currentLastRead
        if (existing != null && existing.surahNumber != surahNumber) {
            setState { copy(showOverwriteDialog = true, pendingVerseKey = verse.verseKey) }
        } else {
            saveLastRead(verse)
        }
    }

    private fun confirmOverwrite() {
        val pendingKey = uiState.value.pendingVerseKey ?: return
        val verse = uiState.value.juz?.verses?.find { it.verseKey == pendingKey } ?: return
        dismissOverwriteDialog()
        saveLastRead(verse)
    }

    private fun dismissOverwriteDialog() {
        setState { copy(showOverwriteDialog = false, pendingVerseKey = null) }
    }

    private fun saveLastRead(verse: JuzVerse) {
        viewModelScope.launch {
            repository.saveLastRead(
                LastRead(
                    surahNumber = verse.surahNumber,
                    ayahNumber = verse.ayahNumber,
                    surahName = verse.surahName,
                    timestamp = System.currentTimeMillis(),
                )
            )
        }
    }

    override fun onCleared() {
        PlaybackForegroundService.stop(application)
        exoPlayer.release()
        mediaSession.release()
        super.onCleared()
    }
}
