package id.vanard.ayatqu

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.vanard.ayatqu.core.ui.theme.AyatQuTheme
import id.vanard.ayatqu.ui.navigation.AppNavGraph
import id.vanard.ayatqu.util.PermissionHelper
import id.vanard.ayatqu.viewmodel.AppViewModel
import id.vanard.ayatqu.viewmodel.StartDestination
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val viewModel: AppViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // Hold splash screen until the start destination is resolved
        splashScreen.setKeepOnScreenCondition {
            viewModel.startDestination.value == StartDestination.Loading
        }

        enableEdgeToEdge()
        setContent {
            AyatQuTheme {
                val startDestination by viewModel.startDestination.collectAsStateWithLifecycle()

                // Request notification permission on Android 13+
                val notificationPermissionLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission()
                ) { isGranted: Boolean ->
                    // Permission result handled - Chucker will work if granted
                }

                LaunchedEffect(Unit) {
                    if (PermissionHelper.isNotificationPermissionRequired(this@MainActivity) &&
                        !PermissionHelper.isNotificationPermissionGranted(this@MainActivity)
                    ) {
                        notificationPermissionLauncher.launch(
                            PermissionHelper.getNotificationPermission()
                        )
                    }
                }

                AppNavGraph(
                    startDestination = startDestination,
                    onOnboardingFinished = viewModel::completeOnboarding
                )
            }
        }
    }
}
