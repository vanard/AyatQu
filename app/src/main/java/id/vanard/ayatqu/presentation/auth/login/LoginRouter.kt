package id.vanard.ayatqu.presentation.auth.login

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.vanard.ayatqu.core.navigation.NavigationManager
import id.vanard.ayatqu.navigation.directions.AuthDirection
import id.vanard.ayatqu.presentation.auth.component.requestGoogleIdToken
import id.vanard.ayatqu.presentation.auth.login.contract.LoginEvent
import id.vanard.ayatqu.presentation.auth.login.contract.LoginSideEffect
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun LoginRouter(
    modifier: Modifier = Modifier,
    viewModel: LoginViewModel = koinViewModel(),
    navigationManager: NavigationManager = koinInject(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(viewModel) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                LoginSideEffect.NavigateBack -> navigationManager.navigate(AuthDirection.back)
                LoginSideEffect.NavigateToSignUp -> navigationManager.navigate(AuthDirection.signUp)
                LoginSideEffect.NavigateToHome -> navigationManager.navigate(AuthDirection.home)
                LoginSideEffect.RequestGoogleSignIn -> {
                    requestGoogleIdToken(context).fold(
                        onSuccess = { viewModel.onEvent(LoginEvent.GoogleTokenReceived(it)) },
                        onFailure = {
                            viewModel.onEvent(
                                LoginEvent.GoogleSignInFailed(it.message ?: "Google sign-in failed")
                            )
                        },
                    )
                }
                is LoginSideEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    LoginScreen(
        state = state,
        onEvent = viewModel::onEvent,
        modifier = modifier,
        snackbarHostState = snackbarHostState,
    )
}
