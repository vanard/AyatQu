package id.vanard.ayatqu.presentation.auth.login

import android.util.Patterns
import androidx.lifecycle.viewModelScope
import id.vanard.ayatqu.domain.usecase.AuthUseCase
import id.vanard.ayatqu.presentation.auth.login.contract.LoginEvent
import id.vanard.ayatqu.presentation.auth.login.contract.LoginSideEffect
import id.vanard.ayatqu.presentation.auth.login.contract.LoginState
import id.vanard.ayatqu.presentation.common.viewmodel.BaseMviViewModel
import kotlinx.coroutines.launch

class LoginViewModel(
    private val authUseCase: AuthUseCase,
) : BaseMviViewModel<LoginState, LoginEvent, LoginSideEffect>(LoginState()) {
    override fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.EmailChanged -> setState { copy(email = event.value, errorMessage = null) }
            is LoginEvent.PasswordChanged -> setState { copy(password = event.value, errorMessage = null) }
            LoginEvent.PasswordVisibilityClicked -> setState { copy(passwordVisible = !passwordVisible) }
            LoginEvent.SubmitClicked -> signIn()
            LoginEvent.GoogleClicked -> setEffect(LoginSideEffect.RequestGoogleSignIn)
            is LoginEvent.GoogleTokenReceived -> signInWithGoogle(event.token)
            is LoginEvent.GoogleSignInFailed -> showError(event.message)
            LoginEvent.BackClicked -> setEffect(LoginSideEffect.NavigateBack)
            LoginEvent.SignUpClicked -> setEffect(LoginSideEffect.NavigateToSignUp)
        }
    }

    private fun signIn() {
        val state = uiState.value
        val validationError = validate(state.email, state.password)
        if (validationError != null) {
            showError(validationError)
            return
        }
        viewModelScope.launch {
            setState { copy(isLoading = true, errorMessage = null) }
            authUseCase.signIn(state.email, state.password).fold(
                onSuccess = {
                    setState { copy(isLoading = false) }
                    setEffect(LoginSideEffect.NavigateToHome)
                },
                onFailure = { error -> showError(error.message ?: "Sign-in failed") },
            )
        }
    }

    private fun signInWithGoogle(token: String) {
        viewModelScope.launch {
            setState { copy(isLoading = true, errorMessage = null) }
            authUseCase.signInWithGoogle(token).fold(
                onSuccess = {
                    setState { copy(isLoading = false) }
                    setEffect(LoginSideEffect.NavigateToHome)
                },
                onFailure = { error -> showError(error.message ?: "Google sign-in failed") },
            )
        }
    }

    private fun showError(message: String) {
        setState { copy(isLoading = false, errorMessage = message) }
        setEffect(LoginSideEffect.ShowMessage(message))
    }

    private fun validate(email: String, password: String): String? = when {
        email.isBlank() -> "Email cannot be empty"
        !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Enter a valid email address"
        password.length < 6 -> "Password must be at least 6 characters"
        else -> null
    }
}
