package id.vanard.ayatqu.presentation.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.vanard.ayatqu.core.navigation.NavigationManager
import id.vanard.ayatqu.navigation.directions.AuthDirection
import id.vanard.ayatqu.presentation.onboarding.contract.OnboardingSideEffect
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun OnboardingRouter(
    modifier: Modifier = Modifier,
    viewModel: OnboardingViewModel = koinViewModel(),
    navigationManager: NavigationManager = koinInject(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                OnboardingSideEffect.NavigateToLanding -> navigationManager.navigate(AuthDirection.landing)
            }
        }
    }

    OnboardingScreen(
        state = state,
        onEvent = viewModel::onEvent,
        modifier = modifier,
    )
}
