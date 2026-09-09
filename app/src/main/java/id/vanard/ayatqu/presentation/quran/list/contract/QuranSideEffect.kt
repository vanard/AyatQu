package id.vanard.ayatqu.presentation.quran.list.contract

sealed interface QuranSideEffect {
    data class NavigateToSurah(val surahNumber: Int) : QuranSideEffect
}
