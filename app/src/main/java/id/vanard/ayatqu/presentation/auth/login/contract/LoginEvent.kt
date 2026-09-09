package id.vanard.ayatqu.presentation.auth.login.contract

sealed interface LoginEvent {
    data class EmailChanged(val value: String) : LoginEvent
    data class PasswordChanged(val value: String) : LoginEvent
    data object PasswordVisibilityClicked : LoginEvent
    data object SubmitClicked : LoginEvent
    data object GoogleClicked : LoginEvent
    data class GoogleTokenReceived(val token: String) : LoginEvent
    data class GoogleSignInFailed(val message: String) : LoginEvent
    data object BackClicked : LoginEvent
    data object SignUpClicked : LoginEvent
}

typealias OnLoginEvent = (LoginEvent) -> Unit
