package id.vanard.ayatqu.presentation.quran.list.contract

sealed interface QuranEvent {
    data class QueryChanged(val query: String) : QuranEvent
    data class TabSelected(val index: Int) : QuranEvent
    data class SurahClicked(val surahNumber: Int) : QuranEvent
    data object ClearQueryClicked : QuranEvent
    data object RetryClicked : QuranEvent
}

typealias OnQuranEvent = (QuranEvent) -> Unit
