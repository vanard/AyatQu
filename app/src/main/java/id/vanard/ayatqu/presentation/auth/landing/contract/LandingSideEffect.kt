package id.vanard.ayatqu.presentation.auth.landing.contract

sealed interface LandingSideEffect {
    data object NavigateToLogin : LandingSideEffect
    data object NavigateToSignUp : LandingSideEffect
    data object NavigateToHome : LandingSideEffect
}
