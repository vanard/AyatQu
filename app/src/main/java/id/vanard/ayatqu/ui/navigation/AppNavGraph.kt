package id.vanard.ayatqu.ui.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import id.vanard.ayatqu.R
import id.vanard.ayatqu.ui.screen.HomeScreen
import id.vanard.ayatqu.ui.screen.LandingScreen
import id.vanard.ayatqu.ui.screen.LoginScreen
import id.vanard.ayatqu.ui.screen.OnboardingScreen
import id.vanard.ayatqu.ui.screen.ProfileScreen
import id.vanard.ayatqu.ui.screen.QuranScreen
import id.vanard.ayatqu.ui.screen.SignUpScreen
import id.vanard.ayatqu.viewmodel.StartDestination

private const val ROUTE_ONBOARDING = "onboarding"
private const val ROUTE_LANDING    = "landing"
private const val ROUTE_LOGIN      = "login"
private const val ROUTE_SIGNUP     = "signup"
private const val ROUTE_MAIN       = "main"

enum class BottomNavDestination(val label: String, val icon: Int) {
    HOME("Home", R.drawable.ic_home),
    QURAN("Quran", R.drawable.ic_favorite),
    PROFILE("Profile", R.drawable.ic_account_box),
}

@Composable
fun AppNavGraph(
    startDestination: StartDestination,
    onOnboardingFinished: () -> Unit,
) {
    val navController = rememberNavController()

    val initialRoute = when (startDestination) {
        StartDestination.Onboarding -> ROUTE_ONBOARDING
        StartDestination.Landing    -> ROUTE_LANDING
        StartDestination.Home       -> ROUTE_MAIN
        StartDestination.Loading    -> ROUTE_LANDING // unreachable — guarded in MainActivity
    }

    NavHost(navController = navController, startDestination = initialRoute) {

        // ── Onboarding ────────────────────────────────────────────────────────
        composable(ROUTE_ONBOARDING) {
            OnboardingScreen(
                onFinish = {
                    onOnboardingFinished()
                    navController.navigate(ROUTE_LANDING) {
                        popUpTo(ROUTE_ONBOARDING) { inclusive = true }
                    }
                }
            )
        }

        // ── Landing ───────────────────────────────────────────────────────────
        composable(ROUTE_LANDING) {
            LandingScreen(
                onLoginClick = { navController.navigate(ROUTE_LOGIN) },
                onSignUpClick = { navController.navigate(ROUTE_SIGNUP) }
            )
        }

        // ── Login ─────────────────────────────────────────────────────────────
        composable(ROUTE_LOGIN) {
            LoginScreen(
                onBackClick = { navController.popBackStack() },
                onNavigateToHome = {
                    navController.navigate(ROUTE_MAIN) {
                        popUpTo(ROUTE_LANDING) { inclusive = true }
                    }
                },
                onSignUpClick = {
                    navController.navigate(ROUTE_SIGNUP) {
                        popUpTo(ROUTE_LOGIN) { inclusive = true }
                    }
                }
            )
        }

        // ── Sign Up ───────────────────────────────────────────────────────────
        composable(ROUTE_SIGNUP) {
            SignUpScreen(
                onBackClick = { navController.popBackStack() },
                onNavigateToHome = {
                    navController.navigate(ROUTE_MAIN) {
                        popUpTo(ROUTE_LANDING) { inclusive = true }
                    }
                },
                onLoginClick = {
                    navController.navigate(ROUTE_LOGIN) {
                        popUpTo(ROUTE_SIGNUP) { inclusive = true }
                    }
                }
            )
        }

        // ── Main (bottom nav) ─────────────────────────────────────────────────
        composable(ROUTE_MAIN) {
            MainScreen(
                onLogout = {
                    navController.navigate(ROUTE_LANDING) {
                        popUpTo(ROUTE_MAIN) { inclusive = true }
                    }
                }
            )
        }
    }
}

@Composable
private fun MainScreen(
    onLogout: () -> Unit = {},
) {
    var current by rememberSaveable { mutableStateOf(BottomNavDestination.HOME) }

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            BottomNavDestination.entries.forEach { dest ->
                item(
                    icon = { Icon(painterResource(dest.icon), contentDescription = dest.label) },
                    label = { Text(dest.label) },
                    selected = current == dest,
                    onClick = { current = dest }
                )
            }
        }
    ) {
        when (current) {
            BottomNavDestination.HOME    -> HomeScreen()
            BottomNavDestination.QURAN   -> QuranScreen()
            BottomNavDestination.PROFILE -> ProfileScreen(onLogout = onLogout)
        }
    }
}
