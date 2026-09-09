package id.vanard.ayatqu.presentation.home

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.vanard.ayatqu.core.navigation.NavigationManager
import id.vanard.ayatqu.navigation.directions.QuranDirection
import id.vanard.ayatqu.presentation.home.contract.HomeEvent
import id.vanard.ayatqu.presentation.home.contract.HomeSideEffect
import id.vanard.ayatqu.util.LocationHelper
import id.vanard.ayatqu.util.PermissionHelper
import id.vanard.ayatqu.worker.AdhanSchedulerWorker
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun HomeRouter(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = koinViewModel(),
    navigationManager: NavigationManager = koinInject(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) { permissions ->
        viewModel.onEvent(HomeEvent.LoadPrayerTimes(permissions.values.any { it }))
    }
    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) AdhanSchedulerWorker.runNow(context)
    }

    LaunchedEffect(Unit) {
        if (LocationHelper.isLocationPermissionGranted(context)) {
            viewModel.onEvent(HomeEvent.LoadPrayerTimes(fetchLocation = true))
        } else {
            locationPermissionLauncher.launch(LocationHelper.getLocationPermissions())
        }
        if (PermissionHelper.isNotificationPermissionRequired(context) &&
            !PermissionHelper.isNotificationPermissionGranted(context)
        ) {
            notificationPermissionLauncher.launch(PermissionHelper.getNotificationPermission())
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                is HomeSideEffect.NavigateToLastRead -> navigationManager.navigate(
                    QuranDirection.detail(effect.surahNumber, effect.ayahNumber)
                )
                is HomeSideEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        HomeScreen(
            state = state,
            onEvent = viewModel::onEvent,
        )
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}
