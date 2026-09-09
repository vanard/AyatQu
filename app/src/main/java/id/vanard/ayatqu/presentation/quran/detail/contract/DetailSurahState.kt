package id.vanard.ayatqu.presentation.quran.detail.contract

import id.vanard.ayatqu.domain.model.Ayah
import id.vanard.ayatqu.domain.model.LastRead
import id.vanard.ayatqu.domain.model.Surah

data class DetailSurahState(
    val isLoading: Boolean = true,
    val surah: Surah? = null,
    val ayahs: List<Ayah> = emptyList(),
    val errorMessage: String? = null,
    val playingAyah: Int? = null,
    val isPreparingAudio: Boolean = false,
    val downloadedAyahs: Set<Int> = emptySet(),
    val downloadingAyahs: Set<Int> = emptySet(),
    val isDownloadingAll: Boolean = false,
    val downloadProgress: Pair<Int, Int>? = null,
    val currentLastRead: LastRead? = null,
    val showOverwriteDialog: Boolean = false,
    val pendingAyahNumber: Int? = null,
)
