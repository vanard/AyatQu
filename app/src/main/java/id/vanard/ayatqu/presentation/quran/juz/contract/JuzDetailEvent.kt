package id.vanard.ayatqu.presentation.quran.juz.contract

sealed interface JuzDetailEvent {
    data class InitialData(val juzNumber: Int) : JuzDetailEvent
    data class VerseClicked(val surahNumber: Int, val ayahNumber: Int) : JuzDetailEvent
    data class PlayAyahClicked(val surahNumber: Int, val ayahNumber: Int) : JuzDetailEvent
    data class DownloadAyahClicked(val surahNumber: Int, val ayahNumber: Int) : JuzDetailEvent
    data class SetLastReadClicked(val surahNumber: Int, val ayahNumber: Int) : JuzDetailEvent
    data object DownloadAllClicked : JuzDetailEvent
    data object StopPlaybackClicked : JuzDetailEvent
    data object ConfirmOverwriteClicked : JuzDetailEvent
    data object DismissOverwriteClicked : JuzDetailEvent
    data object ErrorConsumed : JuzDetailEvent
    data object RetryClicked : JuzDetailEvent
    data object BackClicked : JuzDetailEvent
}

typealias OnJuzDetailEvent = (JuzDetailEvent) -> Unit
