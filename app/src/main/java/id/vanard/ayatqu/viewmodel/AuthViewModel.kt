package id.vanard.ayatqu.viewmodel

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseUser
import id.vanard.ayatqu.BuildConfig
import id.vanard.ayatqu.domain.usecase.AuthUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ── UI State ──────────────────────────────────────────────────────────────────

data class AuthUiState(
    val isLoading: Boolean = false,
    val user: FirebaseUser? = null,
    val error: String? = null,
)

// ── Side effects ──────────────────────────────────────────────────────────────

sealed class AuthEvent {
    data object NavigateToHome : AuthEvent()
    data class ShowError(val message: String) : AuthEvent()
}

// ── ViewModel ─────────────────────────────────────────────────────────────────

class AuthViewModel(private val authUseCase: AuthUseCase) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState(user = authUseCase.currentUser))
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<AuthEvent>()
    val events: SharedFlow<AuthEvent> = _events.asSharedFlow()

    // ── Email / Password ──────────────────────────────────────────────────────

    fun signIn(email: String, password: String) {
        if (!validate(email, password)) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            authUseCase.signIn(email, password)
                .onSuccess { user ->
                    _uiState.update { it.copy(isLoading = false, user = user) }
                    _events.emit(AuthEvent.NavigateToHome)
                }
                .onFailure { e ->
                    val msg = e.message ?: "Sign-in failed"
                    _uiState.update { it.copy(isLoading = false, error = msg) }
                    _events.emit(AuthEvent.ShowError(msg))
                }
        }
    }

    fun signUp(email: String, password: String) {
        if (!validate(email, password)) return
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            authUseCase.signUp(email, password)
                .onSuccess { user ->
                    _uiState.update { it.copy(isLoading = false, user = user) }
                    _events.emit(AuthEvent.NavigateToHome)
                }
                .onFailure { e ->
                    val msg = e.message ?: "Sign-up failed"
                    _uiState.update { it.copy(isLoading = false, error = msg) }
                    _events.emit(AuthEvent.ShowError(msg))
                }
        }
    }

    // ── Google SSO ────────────────────────────────────────────────────────────

    fun signInWithGoogle(context: Context) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching {
                val credentialManager = CredentialManager.create(context)
                val googleIdOption = GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(BuildConfig.GOOGLE_WEB_CLIENT_ID)
                    .build()
                val request = GetCredentialRequest.Builder()
                    .addCredentialOption(googleIdOption)
                    .build()
                val result = credentialManager.getCredential(context, request)
                val googleCredential = GoogleIdTokenCredential.createFrom(result.credential.data)
                googleCredential.idToken
            }.fold(
                onSuccess = { idToken ->
                    authUseCase.signInWithGoogle(idToken)
                        .onSuccess { user ->
                            _uiState.update { it.copy(isLoading = false, user = user) }
                            _events.emit(AuthEvent.NavigateToHome)
                        }
                        .onFailure { e ->
                            val msg = e.message ?: "Google sign-in failed"
                            _uiState.update { it.copy(isLoading = false, error = msg) }
                            _events.emit(AuthEvent.ShowError(msg))
                        }
                },
                onFailure = { e ->
                    val msg = e.message ?: "Google sign-in failed"
                    _uiState.update { it.copy(isLoading = false, error = msg) }
                    _events.emit(AuthEvent.ShowError(msg))
                }
            )
        }
    }

    // ── Sign out ──────────────────────────────────────────────────────────────

    fun signOut() {
        viewModelScope.launch {
            authUseCase.signOut()
            _uiState.update { it.copy(user = null) }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    // ── Validation ────────────────────────────────────────────────────────────

    private fun validate(email: String, password: String): Boolean {
        return when {
            email.isBlank() -> {
                _uiState.update { it.copy(error = "Email cannot be empty") }
                false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                _uiState.update { it.copy(error = "Enter a valid email address") }
                false
            }
            password.length < 6 -> {
                _uiState.update { it.copy(error = "Password must be at least 6 characters") }
                false
            }
            else -> true
        }
    }
}
