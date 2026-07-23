package id.vanard.ayatqu.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import id.vanard.ayatqu.data.local.AyahAudioCache
import id.vanard.ayatqu.data.local.SurahLocalCache
import id.vanard.ayatqu.domain.model.Ayah
import id.vanard.ayatqu.domain.model.Surah
import id.vanard.ayatqu.domain.repository.QuranRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
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

    private val exoPlayer: ExoPlayer = ExoPlayer.Builder(application).build()

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

            // Load title from surah list cache instantly
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

            // Load full detail (ayahs + audio) from cache or network
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

        // Only play if downloaded locally
        if (ayahNumber !in state.downloadedAyahs) return

        // If already playing this ayah, pause
        if (state.playingAyah == ayahNumber) {
            exoPlayer.pause()
            _uiState.update { it.copy(playingAyah = null) }
            return
        }

        exoPlayer.stop()

        val localPath = audioCache.getLocalPath(surahNumber, ayahNumber) ?: return

        val mediaItem = MediaItem.fromUri("file://$localPath")
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()

        _uiState.update { it.copy(playingAyah = ayahNumber) }
    }

    private fun playNextAyah() {
        val state = _uiState.value
        val current = state.playingAyah ?: return
        val next = current + 1
        if (next <= (state.surah?.versesCount ?: 0) && next in state.downloadedAyahs) {
            playAyah(next)
        } else {
            _uiState.update { it.copy(playingAyah = null) }
        }
    }

    fun stopPlayback() {
        exoPlayer.stop()
        _uiState.update { it.copy(playingAyah = null) }
    }

    fun downloadAyah(ayahNumber: Int) {
        val state = _uiState.value
        if (ayahNumber in state.downloadingAyahs) return
        if (ayahNumber in state.downloadedAyahs) return

        val ayah = state.ayahs.find { it.ayahNumber == ayahNumber } ?: return
        val audioUrl = ayah.audioUrls.firstOrNull()?.ayahAudioUrl
            ?: ayah.audioUrls.firstOrNull()?.surahAudioUrl
            ?: return

        _uiState.update { it.copy(downloadingAyahs = it.downloadingAyahs + ayahNumber) }

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
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
                    val audioUrl = ayah.audioUrls.firstOrNull()?.ayahAudioUrl
                        ?: ayah.audioUrls.firstOrNull()?.surahAudioUrl
                        ?: return@forEachIndexed
                    try {
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
        exoPlayer.release()
    }
}
