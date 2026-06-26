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
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {

    private val viewModel: AppViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // Hold splash until we know onboarding state
        splashScreen.setKeepOnScreenCondition {
            viewModel.isOnboardingDone.value == null
        }

        enableEdgeToEdge()
        setContent {
            AyatQuTheme {
                val isOnboardingDone by viewModel.isOnboardingDone.collectAsStateWithLifecycle()
                isOnboardingDone?.let {
                    AppNavGraph(
                        isOnboardingDone = it,
                        onOnboardingFinished = viewModel::completeOnboarding
                    )
                }
            }
        }
    }
}
