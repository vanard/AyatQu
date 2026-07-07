package id.vanard.ayatqu.ui.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import id.vanard.ayatqu.core.ui.theme.AyatQuTheme
import id.vanard.ayatqu.ui.icons.BookOpen
import id.vanard.ayatqu.ui.icons.BookOpenFill
import id.vanard.ayatqu.ui.icons.House
import id.vanard.ayatqu.ui.icons.HouseFill
import id.vanard.ayatqu.ui.icons.UserCircle
import id.vanard.ayatqu.ui.icons.UserCircleFill
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

// ── Bottom nav design tokens ──────────────────────────────────────────────────
private val NavBarSurface       = Color(0xFFFFFFFF)
private val NavBarDivider       = Color(0xFFF0F2F5)
private val NavItemActive       = Color(0xFF2D6B8C)
private val NavItemActiveBg     = Color(0x142D6B8C)  // ~8% Primary
private val NavItemInactive     = Color(0xFF8E8E93)

enum class BottomNavDestination(
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
) {
    HOME("Home", HouseFill, House),
    QURAN("Quran", BookOpenFill, BookOpen),
    PROFILE("Profile", UserCircleFill, UserCircle),
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

// ── Main with custom bottom bar ───────────────────────────────────────────────

@Composable
private fun MainScreen(
    onLogout: () -> Unit = {},
) {
    var current by rememberSaveable { mutableStateOf(BottomNavDestination.HOME) }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(1f)) {
            when (current) {
                BottomNavDestination.HOME    -> HomeScreen()
                BottomNavDestination.QURAN   -> QuranScreen()
                BottomNavDestination.PROFILE -> ProfileScreen(onLogout = onLogout)
            }
        }

        AyatQuBottomBar(
            current = current,
            onSelect = { current = it },
        )
    }
}

// ── Custom bottom bar with Phosphor icons ─────────────────────────────────────

@Composable
fun AyatQuBottomBar(
    current: BottomNavDestination,
    onSelect: (BottomNavDestination) -> Unit,
) {
    Surface(
        color = NavBarSurface,
        tonalElevation = 0.dp,
        shadowElevation = 8.dp,
    ) {
        Column {
            HorizontalDivider(
                color = NavBarDivider,
                thickness = 0.5.dp,
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = 8.dp,
                        vertical = 8.dp,
                    )
                    .padding(
                        WindowInsets.navigationBars.asPaddingValues()
                    ),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BottomNavDestination.entries.forEach { dest ->
                    NavBarItem(
                        destination = dest,
                        selected = current == dest,
                        onClick = { onSelect(dest) },
                    )
                }
            }
        }
    }
}

@Composable
private fun NavBarItem(
    destination: BottomNavDestination,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val iconColor by animateColorAsState(
        targetValue = if (selected) NavItemActive else NavItemInactive,
        animationSpec = tween(durationMillis = 200),
        label = "iconColor",
    )
    val labelColor by animateColorAsState(
        targetValue = if (selected) NavItemActive else NavItemInactive,
        animationSpec = tween(durationMillis = 200),
        label = "labelColor",
    )

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(if (selected) NavItemActiveBg else Color.Transparent)
                .padding(horizontal = 12.dp, vertical = 6.dp),
        ) {
            Icon(
                imageVector = if (selected) destination.selectedIcon else destination.unselectedIcon,
                contentDescription = destination.label,
                tint = iconColor,
                modifier = Modifier.size(24.dp),
            )
        }

        Text(
            text = destination.label,
            color = labelColor,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
        )
    }
}

// ── Preview ───────────────────────────────────────────────────────────────────

@Preview(showBackground = true, name = "Bottom Bar")
@Composable
fun BottomBarPreview() {
    AyatQuTheme(darkTheme = false) {
        var current by rememberSaveable { mutableStateOf(BottomNavDestination.HOME) }
        AyatQuBottomBar(current = current, onSelect = { current = it })
    }
}
