package id.vanard.ayatqu.ui.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
    isOnboardingDone: Boolean,
    onOnboardingFinished: () -> Unit,
) {
    val navController = rememberNavController()
    val startDestination = if (isOnboardingDone) ROUTE_LANDING else ROUTE_ONBOARDING

    NavHost(navController = navController, startDestination = startDestination) {
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
        composable(ROUTE_LANDING) {
            LandingScreen(
                onLoginClick = { navController.navigate(ROUTE_LOGIN) },
                onSignUpClick = { navController.navigate(ROUTE_SIGNUP) }
            )
        }
        composable(ROUTE_LOGIN) {
            LoginScreen(
                onBackClick = { navController.popBackStack() },
                onLoginClick = { _, _ ->
                    navController.navigate(ROUTE_MAIN) {
                        popUpTo(ROUTE_LANDING) { inclusive = true }
                    }
                },
                onSignUpClick = {
                    navController.navigate(ROUTE_SIGNUP) {
                        popUpTo(ROUTE_LOGIN) { inclusive = true }
                    }
                },
                onGoogleClick = {
                    navController.navigate(ROUTE_MAIN) {
                        popUpTo(ROUTE_LANDING) { inclusive = true }
                    }
                }
            )
        }
        composable(ROUTE_SIGNUP) {
            SignUpScreen(
                onBackClick = { navController.popBackStack() },
                onSignUpClick = { _, _ ->
                    navController.navigate(ROUTE_MAIN) {
                        popUpTo(ROUTE_LANDING) { inclusive = true }
                    }
                },
                onLoginClick = {
                    navController.navigate(ROUTE_LOGIN) {
                        popUpTo(ROUTE_SIGNUP) { inclusive = true }
                    }
                },
                onGoogleClick = {
                    navController.navigate(ROUTE_MAIN) {
                        popUpTo(ROUTE_LANDING) { inclusive = true }
                    }
                },
                onAppleClick = {
                    navController.navigate(ROUTE_MAIN) {
                        popUpTo(ROUTE_LANDING) { inclusive = true }
                    }
                }
            )
        }
        composable(ROUTE_MAIN) {
            MainScreen()
        }
    }
}

@Composable
private fun MainScreen() {
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
            BottomNavDestination.HOME -> HomeScreen()
            BottomNavDestination.QURAN -> QuranScreen()
            BottomNavDestination.PROFILE -> ProfileScreen()
        }
    }
}
