package id.vanard.ayatqu.presentation.auth.signup.contract

sealed interface SignUpSideEffect {
    data object NavigateBack : SignUpSideEffect
    data object NavigateToLogin : SignUpSideEffect
    data object NavigateToHome : SignUpSideEffect
    data object RequestGoogleSignIn : SignUpSideEffect
    data class ShowMessage(val message: String) : SignUpSideEffect
}
