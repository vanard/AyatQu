package id.vanard.ayatqu.presentation.quran.juz.contract

import id.vanard.ayatqu.domain.model.Juz
import id.vanard.ayatqu.domain.model.LastRead

data class JuzDetailState(
    val juzNumber: Int = 1,
    val isLoading: Boolean = false,
    val juz: Juz? = null,
    val errorMessage: String? = null,
    val playingVerseKey: String? = null,
    val isPreparingAudio: Boolean = false,
    val downloadedVerseKeys: Set<String> = emptySet(),
    val downloadingVerseKeys: Set<String> = emptySet(),
    val isDownloadingAll: Boolean = false,
    val downloadProgress: Pair<Int, Int>? = null,
    val currentLastRead: LastRead? = null,
    val showOverwriteDialog: Boolean = false,
    val pendingVerseKey: String? = null,
)
