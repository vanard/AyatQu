package id.vanard.ayatqu.presentation.quran.detail.contract

sealed interface DetailSurahSideEffect {
    data object NavigateBack : DetailSurahSideEffect
    data class ShowMessage(val message: String) : DetailSurahSideEffect
}
