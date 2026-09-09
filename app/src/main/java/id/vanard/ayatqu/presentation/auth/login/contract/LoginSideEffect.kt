package id.vanard.ayatqu.presentation.auth.login.contract

sealed interface LoginSideEffect {
    data object NavigateBack : LoginSideEffect
    data object NavigateToSignUp : LoginSideEffect
    data object NavigateToHome : LoginSideEffect
    data object RequestGoogleSignIn : LoginSideEffect
    data class ShowMessage(val message: String) : LoginSideEffect
}
