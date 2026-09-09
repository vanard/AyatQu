package id.vanard.ayatqu

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import id.vanard.ayatqu.core.ui.theme.AyatQuTheme
import id.vanard.ayatqu.navigation.routes.AuthRoute
import id.vanard.ayatqu.navigation.routes.MainRoute
import id.vanard.ayatqu.presentation.root.AppViewModel
import id.vanard.ayatqu.presentation.root.StartDestination
import id.vanard.ayatqu.presentation.root.navigation.NavigationRoot
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: AppViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // Hold splash screen until the start destination is resolved
        splashScreen.setKeepOnScreenCondition {
            viewModel.startDestination.value == StartDestination.Loading
        }

        enableEdgeToEdge()

        // Match device display refresh rate (min 60fps)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val display = display
            display?.let {
                val modes = it.supportedModes
                val bestMode = modes.maxByOrNull { mode -> mode.refreshRate }
                bestMode?.let { mode ->
                    val params = window.attributes
                    params.preferredDisplayModeId = mode.modeId
                    window.attributes = params
                }
            }
        }
        setContent {
            AyatQuTheme {
                val startDestination by viewModel.startDestination.collectAsStateWithLifecycle()

                when (startDestination) {
                    StartDestination.Loading -> Unit
                    StartDestination.Onboarding -> NavigationRoot(AuthRoute.Onboarding)
                    StartDestination.Landing -> NavigationRoot(AuthRoute.Landing)
                    StartDestination.Home -> NavigationRoot(MainRoute.Home)
                }
            }
        }
    }
}
