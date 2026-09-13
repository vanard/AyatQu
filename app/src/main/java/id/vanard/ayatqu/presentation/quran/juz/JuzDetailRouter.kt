package id.vanard.ayatqu.presentation.quran.juz

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.vanard.ayatqu.core.navigation.NavigationManager
import id.vanard.ayatqu.navigation.directions.QuranDirection
import id.vanard.ayatqu.presentation.quran.juz.contract.JuzDetailEvent
import id.vanard.ayatqu.presentation.quran.juz.contract.JuzDetailSideEffect
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun JuzDetailRouter(
    juzNumber: Int,
    modifier: Modifier = Modifier,
    viewModel: JuzDetailViewModel = koinViewModel(),
    navigationManager: NavigationManager = koinInject(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val listState = rememberLazyListState()
    val snackbarHostState = remember { SnackbarHostState() }
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {}

    LaunchedEffect(juzNumber) {
        viewModel.onEvent(JuzDetailEvent.InitialData(juzNumber))
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
    LaunchedEffect(viewModel) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                JuzDetailSideEffect.NavigateBack -> navigationManager.navigate(QuranDirection.back)
                is JuzDetailSideEffect.NavigateToVerse -> navigationManager.navigate(
                    QuranDirection.detail(effect.surahNumber, effect.ayahNumber)
                )
                is JuzDetailSideEffect.ShowMessage -> {
                    snackbarHostState.showSnackbar(effect.message)
                    viewModel.onEvent(JuzDetailEvent.ErrorConsumed)
                }
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        JuzDetailScreen(
            state = state,
            onEvent = viewModel::onEvent,
            listState = listState,
        )
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}
