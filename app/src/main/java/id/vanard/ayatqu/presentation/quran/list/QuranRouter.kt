package id.vanard.ayatqu.presentation.quran.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.vanard.ayatqu.core.navigation.NavigationManager
import id.vanard.ayatqu.navigation.directions.QuranDirection
import id.vanard.ayatqu.presentation.quran.list.contract.QuranSideEffect
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun QuranRouter(
    modifier: Modifier = Modifier,
    viewModel: QuranViewModel = koinViewModel(),
    navigationManager: NavigationManager = koinInject(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(viewModel) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is QuranSideEffect.NavigateToSurah -> navigationManager.navigate(
                    QuranDirection.detail(effect.surahNumber)
                )
                is QuranSideEffect.NavigateToJuz -> navigationManager.navigate(
                    QuranDirection.juz(effect.juzNumber)
                )
            }
        }
    }

    QuranScreen(
        state = state,
        onEvent = viewModel::onEvent,
        modifier = modifier,
    )
}
