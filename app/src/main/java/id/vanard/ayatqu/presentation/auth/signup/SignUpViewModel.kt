package id.vanard.ayatqu.presentation.auth.signup

import android.util.Patterns
import androidx.lifecycle.viewModelScope
import id.vanard.ayatqu.domain.usecase.AuthUseCase
import id.vanard.ayatqu.presentation.auth.signup.contract.SignUpEvent
import id.vanard.ayatqu.presentation.auth.signup.contract.SignUpSideEffect
import id.vanard.ayatqu.presentation.auth.signup.contract.SignUpState
import id.vanard.ayatqu.presentation.common.viewmodel.BaseMviViewModel
import kotlinx.coroutines.launch

class SignUpViewModel(
    private val authUseCase: AuthUseCase,
) : BaseMviViewModel<SignUpState, SignUpEvent, SignUpSideEffect>(SignUpState()) {
    override fun onEvent(event: SignUpEvent) {
        when (event) {
            is SignUpEvent.EmailChanged -> setState { copy(email = event.value, errorMessage = null) }
            is SignUpEvent.PasswordChanged -> setState { copy(password = event.value, errorMessage = null) }
            SignUpEvent.PasswordVisibilityClicked -> setState { copy(passwordVisible = !passwordVisible) }
            SignUpEvent.SubmitClicked -> signUp()
            SignUpEvent.GoogleClicked -> setEffect(SignUpSideEffect.RequestGoogleSignIn)
            is SignUpEvent.GoogleTokenReceived -> signInWithGoogle(event.token)
            is SignUpEvent.GoogleSignInFailed -> showError(event.message)
            SignUpEvent.BackClicked -> setEffect(SignUpSideEffect.NavigateBack)
            SignUpEvent.LoginClicked -> setEffect(SignUpSideEffect.NavigateToLogin)
        }
    }

    private fun signUp() {
        val state = uiState.value
        val validationError = validate(state.email, state.password)
        if (validationError != null) {
            showError(validationError)
            return
        }
        viewModelScope.launch {
            setState { copy(isLoading = true, errorMessage = null) }
            authUseCase.signUp(state.email, state.password).fold(
                onSuccess = {
                    setState { copy(isLoading = false) }
                    setEffect(SignUpSideEffect.NavigateToHome)
                },
                onFailure = { error -> showError(error.message ?: "Sign-up failed") },
            )
        }
    }

    private fun signInWithGoogle(token: String) {
        viewModelScope.launch {
            setState { copy(isLoading = true, errorMessage = null) }
            authUseCase.signInWithGoogle(token).fold(
                onSuccess = {
                    setState { copy(isLoading = false) }
                    setEffect(SignUpSideEffect.NavigateToHome)
                },
                onFailure = { error -> showError(error.message ?: "Google sign-in failed") },
            )
        }
    }

    private fun showError(message: String) {
        setState { copy(isLoading = false, errorMessage = message) }
        setEffect(SignUpSideEffect.ShowMessage(message))
    }

    private fun validate(email: String, password: String): String? = when {
        email.isBlank() -> "Email cannot be empty"
        !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Enter a valid email address"
        password.length < 6 -> "Password must be at least 6 characters"
        else -> null
    }
}
