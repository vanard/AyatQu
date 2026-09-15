package id.vanard.ayatqu.presentation.home.contract

sealed interface HomeSideEffect {
    data class NavigateToLastRead(val surahNumber: Int, val ayahNumber: Int) : HomeSideEffect
    data object NavigateToQibla : HomeSideEffect
    data class ShowMessage(val message: String) : HomeSideEffect
}
