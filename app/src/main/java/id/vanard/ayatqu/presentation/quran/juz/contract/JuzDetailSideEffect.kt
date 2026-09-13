package id.vanard.ayatqu.presentation.quran.juz.contract

sealed interface JuzDetailSideEffect {
    data object NavigateBack : JuzDetailSideEffect
    data class NavigateToVerse(
        val surahNumber: Int,
        val ayahNumber: Int,
    ) : JuzDetailSideEffect
    data class ShowMessage(val message: String) : JuzDetailSideEffect
}
