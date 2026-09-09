package id.vanard.ayatqu.presentation.profile

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.Lifecycle
import id.vanard.ayatqu.core.navigation.NavigationManager
import id.vanard.ayatqu.navigation.directions.AuthDirection
import id.vanard.ayatqu.presentation.profile.contract.ProfileEvent
import id.vanard.ayatqu.presentation.profile.contract.ProfileSideEffect
import id.vanard.ayatqu.util.PermissionHelper
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun ProfileRouter(
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = koinViewModel(),
    navigationManager: NavigationManager = koinInject(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var awaitingExactAlarmPermission by rememberSaveable { mutableStateOf(false) }

    fun finishNotificationPermission() {
        if (PermissionHelper.canScheduleExactAlarms(context)) {
            viewModel.onEvent(ProfileEvent.NotificationPermissionResult(granted = true))
        } else {
            awaitingExactAlarmPermission = true
            PermissionHelper.openExactAlarmSettings(context)
        }
    }

    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        if (awaitingExactAlarmPermission) {
            if (PermissionHelper.canScheduleExactAlarms(context)) {
                viewModel.onEvent(ProfileEvent.NotificationPermissionResult(granted = true))
            }
            awaitingExactAlarmPermission = false
        }
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        if (granted) finishNotificationPermission()
    }

    LaunchedEffect(viewModel) {
        viewModel.uiEffect.collect { effect ->
            when (effect) {
                ProfileSideEffect.NavigateToLogin -> navigationManager.navigate(AuthDirection.login)
                ProfileSideEffect.NavigateToSignUp -> navigationManager.navigate(AuthDirection.signUp)
                ProfileSideEffect.NavigateToLanding -> navigationManager.navigate(AuthDirection.landing)
                ProfileSideEffect.EnableNotifications -> {
                    if (PermissionHelper.isNotificationPermissionGranted(context)) {
                        finishNotificationPermission()
                    } else {
                        notificationPermissionLauncher.launch(
                            PermissionHelper.getNotificationPermission()
                        )
                    }
                }
                is ProfileSideEffect.ShowMessage -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        ProfileScreen(
            state = state,
            onEvent = viewModel::onEvent,
        )
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}
