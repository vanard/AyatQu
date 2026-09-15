package id.vanard.ayatqu.presentation.qibla.contract

sealed interface QiblaSideEffect {
    data object NavigateBack : QiblaSideEffect
    data object RequestLocationPermission : QiblaSideEffect
}
