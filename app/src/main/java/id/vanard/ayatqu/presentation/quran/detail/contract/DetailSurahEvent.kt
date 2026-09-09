package id.vanard.ayatqu.presentation.quran.detail.contract

sealed interface DetailSurahEvent {
    data class InitialData(val surahNumber: Int) : DetailSurahEvent
    data class PlayAyahClicked(val ayahNumber: Int) : DetailSurahEvent
    data class DownloadAyahClicked(val ayahNumber: Int) : DetailSurahEvent
    data class SetLastReadClicked(val ayahNumber: Int) : DetailSurahEvent
    data object DownloadAllClicked : DetailSurahEvent
    data object StopPlaybackClicked : DetailSurahEvent
    data object ConfirmOverwriteClicked : DetailSurahEvent
    data object DismissOverwriteClicked : DetailSurahEvent
    data object BackClicked : DetailSurahEvent
    data object ErrorConsumed : DetailSurahEvent
}

typealias OnDetailSurahEvent = (DetailSurahEvent) -> Unit
