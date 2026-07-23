package id.vanard.ayatqu.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import id.vanard.ayatqu.data.local.AyahAudioCache
import id.vanard.ayatqu.data.local.SurahLocalCache
import id.vanard.ayatqu.domain.model.Ayah
import id.vanard.ayatqu.domain.model.Surah
import id.vanard.ayatqu.domain.repository.QuranRepository
import id.vanard.ayatqu.service.PlaybackForegroundService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.URL

data class DetailSurahUiState(
    val isLoading: Boolean = true,
    val surah: Surah? = null,
    val ayahs: List<Ayah> = emptyList(),
    val error: String? = null,
    val playingAyah: Int? = null,
    val isPreparingAudio: Boolean = false,
    val downloadedAyahs: Set<Int> = emptySet(),
    val downloadingAyahs: Set<Int> = emptySet(),
    val isDownloadingAll: Boolean = false,
    val downloadProgress: Pair<Int, Int>? = null,
)

class DetailSurahViewModel(
    application: Application,
    private val repository: QuranRepository,
    private val audioCache: AyahAudioCache,
    private val surahLocalCache: SurahLocalCache,
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(DetailSurahUiState())
    val uiState: StateFlow<DetailSurahUiState> = _uiState.asStateFlow()

    private val exoPlayer: ExoPlayer = ExoPlayer.Builder(application)
        .setAudioAttributes(AudioAttributes.DEFAULT, true)
        .setHandleAudioBecomingNoisy(true)
        .setWakeMode(C.WAKE_MODE_NETWORK)
        .build()

    private val mediaSession: MediaSession = MediaSession.Builder(application, exoPlayer).build()

    private var surahNumber: Int = 0

    init {
        exoPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_BUFFERING -> _uiState.update { it.copy(isPreparingAudio = true) }
                    Player.STATE_READY -> _uiState.update { it.copy(isPreparingAudio = false) }
                    Player.STATE_ENDED -> {
                        _uiState.update { it.copy(isPreparingAudio = false) }
                        playNextAyah()
                    }
                }
            }

            override fun onIsPlayingChanged(isPlaying: Boolean) {
                if (!isPlaying && exoPlayer.currentPosition >= exoPlayer.duration - 500) {
                    playNextAyah()
                }
            }
        })
    }

    fun loadSurah(number: Int) {
        surahNumber = number
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val cachedSurahs = surahLocalCache.read()
            val cachedSurah = cachedSurahs?.find { it.number == number }
            if (cachedSurah != null) {
                _uiState.update {
                    it.copy(
                        surah = Surah(
                            number = cachedSurah.number,
                            nameArabic = cachedSurah.nameArabic,
                            nameEnglish = cachedSurah.nameEnglish,
                            nameTranslation = cachedSurah.nameTranslation,
                            revelationPlace = cachedSurah.revelationPlace,
                            versesCount = cachedSurah.versesCount,
                            bismillahPre = cachedSurah.bismillahPre,
                        )
                    )
                }
            }

            repository.getSurahWithAyahs(number)
                .onSuccess { (surah, ayahs) ->
                    val downloaded = ayahs.mapNotNull { ayah ->
                        if (audioCache.isDownloaded(number, ayah.ayahNumber)) ayah.ayahNumber else null
                    }.toSet()
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            surah = surah,
                            ayahs = ayahs,
                            downloadedAyahs = downloaded,
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(isLoading = false, error = e.message ?: "Failed to load surah")
                    }
                }
        }
    }

    fun playAyah(ayahNumber: Int) {
        val state = _uiState.value

        if (ayahNumber !in state.downloadedAyahs) return

        if (state.playingAyah == ayahNumber) {
            exoPlayer.pause()
            _uiState.update { it.copy(playingAyah = null) }
            PlaybackForegroundService.stop(getApplication())
            return
        }

        exoPlayer.stop()

        val localPath = audioCache.getLocalPath(surahNumber, ayahNumber) ?: return
        val surahName = state.surah?.nameEnglish ?: "Surah $surahNumber"

        val mediaItem = MediaItem.Builder()
            .setUri("file://$localPath")
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle("$surahName - Ayah $ayahNumber")
                    .setArtist("Quran")
                    .build()
            )
            .build()

        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()

        _uiState.update { it.copy(playingAyah = ayahNumber) }

        // Start foreground service to keep alive when screen locked
        PlaybackForegroundService.start(
            context = getApplication(),
            title = surahName,
            text = "Playing Ayah $ayahNumber",
        )
    }

    private fun playNextAyah() {
        val state = _uiState.value
        val current = state.playingAyah ?: return
        val next = current + 1
        if (next <= (state.surah?.versesCount ?: 0) && next in state.downloadedAyahs) {
            playAyah(next)
        } else {
            _uiState.update { it.copy(playingAyah = null) }
            PlaybackForegroundService.stop(getApplication())
        }
    }

    fun stopPlayback() {
        exoPlayer.stop()
        _uiState.update { it.copy(playingAyah = null) }
        PlaybackForegroundService.stop(getApplication())
    }

    fun downloadAyah(ayahNumber: Int) {
        val state = _uiState.value
        if (ayahNumber in state.downloadingAyahs) return
        if (ayahNumber in state.downloadedAyahs) return

        _uiState.update { it.copy(downloadingAyahs = it.downloadingAyahs + ayahNumber) }

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val audioUrl = repository.getAyahAudioUrl(surahNumber, ayahNumber)
                        ?: throw IOException("Could not get audio URL")

                    val file = audioCache.getAyahFile(surahNumber, ayahNumber)
                    URL(audioUrl).openStream().use { input ->
                        file.outputStream().use { output ->
                            input.copyTo(output)
                        }
                    }
                    audioCache.markDownloaded(surahNumber, ayahNumber, file.absolutePath)
                    _uiState.update {
                        it.copy(
                            downloadedAyahs = it.downloadedAyahs + ayahNumber,
                            downloadingAyahs = it.downloadingAyahs - ayahNumber,
                        )
                    }
                } catch (e: Exception) {
                    _uiState.update {
                        it.copy(
                            downloadingAyahs = it.downloadingAyahs - ayahNumber,
                            error = "Download failed: ${e.message}",
                        )
                    }
                }
            }
        }
    }

    fun downloadAll() {
        val state = _uiState.value
        if (state.isDownloadingAll) return

        val ayahsToDownload = state.ayahs.filter {
            it.ayahNumber !in state.downloadedAyahs && it.ayahNumber !in state.downloadingAyahs
        }
        if (ayahsToDownload.isEmpty()) return

        val downloadingIds = ayahsToDownload.map { it.ayahNumber }.toSet()
        _uiState.update {
            it.copy(
                isDownloadingAll = true,
                downloadingAyahs = it.downloadingAyahs + downloadingIds,
                downloadProgress = 0 to ayahsToDownload.size,
            )
        }

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                ayahsToDownload.forEachIndexed { index, ayah ->
                    try {
                        val audioUrl = repository.getAyahAudioUrl(surahNumber, ayah.ayahNumber)
                            ?: return@forEachIndexed

                        val file = audioCache.getAyahFile(surahNumber, ayah.ayahNumber)
                        URL(audioUrl).openStream().use { input ->
                            file.outputStream().use { output ->
                                input.copyTo(output)
                            }
                        }
                        audioCache.markDownloaded(surahNumber, ayah.ayahNumber, file.absolutePath)
                        _uiState.update {
                            it.copy(
                                downloadedAyahs = it.downloadedAyahs + ayah.ayahNumber,
                                downloadingAyahs = it.downloadingAyahs - ayah.ayahNumber,
                                downloadProgress = (index + 1) to ayahsToDownload.size,
                            )
                        }
                    } catch (_: Exception) {
                        _uiState.update {
                            it.copy(downloadingAyahs = it.downloadingAyahs - ayah.ayahNumber)
                        }
                    }
                }
                _uiState.update { it.copy(isDownloadingAll = false, downloadProgress = null) }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    override fun onCleared() {
        super.onCleared()
        PlaybackForegroundService.stop(getApplication())
        exoPlayer.release()
        mediaSession.release()
    }
}
