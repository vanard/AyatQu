package id.vanard.ayatqu.presentation.auth.signup.contract

sealed interface SignUpEvent {
    data class EmailChanged(val value: String) : SignUpEvent
    data class PasswordChanged(val value: String) : SignUpEvent
    data object PasswordVisibilityClicked : SignUpEvent
    data object SubmitClicked : SignUpEvent
    data object GoogleClicked : SignUpEvent
    data class GoogleTokenReceived(val token: String) : SignUpEvent
    data class GoogleSignInFailed(val message: String) : SignUpEvent
    data object BackClicked : SignUpEvent
    data object LoginClicked : SignUpEvent
}

typealias OnSignUpEvent = (SignUpEvent) -> Unit
