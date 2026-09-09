package id.vanard.ayatqu.presentation.auth.landing

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import id.vanard.ayatqu.core.navigation.NavigationManager
import id.vanard.ayatqu.navigation.directions.AuthDirection
import id.vanard.ayatqu.presentation.auth.landing.contract.LandingSideEffect
import id.vanard.ayatqu.presentation.auth.landing.contract.LandingState
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun LandingRouter(
    modifier: Modifier = Modifier,
    viewModel: LandingViewModel = koinViewModel(),
    navigationManager: NavigationManager = koinInject(),
) {
    LaunchedEffect(viewModel) {
        viewModel.uiEffect.collect { effect ->
            navigationManager.navigate(
                when (effect) {
                    LandingSideEffect.NavigateToLogin -> AuthDirection.login
                    LandingSideEffect.NavigateToSignUp -> AuthDirection.signUp
                    LandingSideEffect.NavigateToHome -> AuthDirection.home
                }
            )
        }
    }

    LandingScreen(
        state = LandingState,
        onEvent = viewModel::onEvent,
        modifier = modifier,
    )
}
