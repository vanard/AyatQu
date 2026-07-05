package id.vanard.ayatqu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.vanard.ayatqu.core.ui.theme.AyatQuTheme
import id.vanard.ayatqu.ui.navigation.AppNavGraph
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

                if (startDestination != StartDestination.Loading) {
                    AppNavGraph(
                        startDestination = startDestination,
                        onOnboardingFinished = viewModel::completeOnboarding
                    )
                }
            }
        }
    }
}
