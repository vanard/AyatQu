package id.vanard.ayatqu.presentation.root.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.ui.NavDisplay
import id.vanard.ayatqu.R
import id.vanard.ayatqu.core.navigation.AppNavigationCommand
import id.vanard.ayatqu.core.navigation.NavigationManager
import id.vanard.ayatqu.core.navigation.rememberNavigationState
import id.vanard.ayatqu.core.ui.theme.AyatQuTheme
import id.vanard.ayatqu.navigation.routes.MainRoute
import org.koin.compose.koinInject

private val topLevelRoutes = linkedSetOf<NavKey>(
    MainRoute.Home,
    MainRoute.Quran,
    MainRoute.Profile,
)

enum class BottomNavDestination(
    val route: NavKey,
    @param:StringRes val labelRes: Int,
    @param:DrawableRes val selectedIconRes: Int,
    @param:DrawableRes val unselectedIconRes: Int,
) {
    HOME(MainRoute.Home, R.string.nav_home, R.drawable.home_05_stroke_rounded, R.drawable.home_05_stroke_rounded),
    QURAN(MainRoute.Quran, R.string.nav_quran, R.drawable.quran_01_stroke_rounded, R.drawable.quran_01_stroke_rounded),
    PROFILE(MainRoute.Profile, R.string.nav_profile, R.drawable.ic_user_circle, R.drawable.ic_user_circle),
}

@Composable
fun NavigationRoot(
    initialRoute: NavKey,
    modifier: Modifier = Modifier,
    navigationManager: NavigationManager = koinInject(),
) {
    val navigationState = rememberNavigationState(
        initialRoute = initialRoute,
        startTopLevelRoute = MainRoute.Home,
        topLevelRoutes = topLevelRoutes,
    )

    LaunchedEffect(navigationManager, navigationState) {
        navigationManager.commands.collect { command ->
            when (command) {
                is AppNavigationCommand.Navigate -> navigationState.navigate(
                    command.route,
                    command.options,
                )
                is AppNavigationCommand.PopBack -> navigationState.popBack()
                is AppNavigationCommand.PopBackTo -> navigationState.popBackTo(
                    command.route,
                    command.inclusive,
                )
                is AppNavigationCommand.ReplaceAll -> navigationState.replaceAll(command.route)
            }
        }
    }

    val currentDestination = BottomNavDestination.entries.firstOrNull {
        it.route == navigationState.currentRoute
    }

    Scaffold(
        modifier = modifier,
        contentWindowInsets = WindowInsets(),
        bottomBar = {
            currentDestination?.let { destination ->
                AyatQuBottomBar(
                    current = destination,
                    onSelect = { navigationState.navigate(it.route) },
                )
            }
        },
    ) { paddingValues ->
        NavDisplay(
            entries = navigationState.toDecoratedEntries(ayatQuEntryProvider),
            onBack = { navigationState.popBack() },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        )
    }
}

@Composable
fun AyatQuBottomBar(
    current: BottomNavDestination,
    onSelect: (BottomNavDestination) -> Unit,
) {
    Surface(
        color = AyatQuTheme.colors.surface,
        tonalElevation = 0.dp,
        shadowElevation = 8.dp,
    ) {
        Column(
            modifier = Modifier.padding(WindowInsets.navigationBars.asPaddingValues()),
        ) {
            HorizontalDivider(color = AyatQuTheme.colors.divider, thickness = 0.5.dp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp)
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BottomNavDestination.entries.forEach { destination ->
                    NavBarItem(
                        destination = destination,
                        selected = current == destination,
                        onClick = { onSelect(destination) },
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
        targetValue = if (selected) AyatQuTheme.colors.primary else AyatQuTheme.colors.textMuted,
        animationSpec = tween(durationMillis = 200),
        label = "iconColor",
    )
    val labelColor by animateColorAsState(
        targetValue = if (selected) AyatQuTheme.colors.primary else AyatQuTheme.colors.textMuted,
        animationSpec = tween(durationMillis = 200),
        label = "labelColor",
    )

    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(if (selected) AyatQuTheme.colors.selectedSurface else Color.Transparent)
                .padding(horizontal = 12.dp, vertical = 6.dp),
        ) {
            Icon(
                painter = painterResource(
                    if (selected) destination.selectedIconRes else destination.unselectedIconRes
                ),
                contentDescription = stringResource(destination.labelRes),
                tint = iconColor,
                modifier = Modifier.size(24.dp),
            )
        }
        Text(
            text = stringResource(destination.labelRes),
            color = labelColor,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
        )
    }
}

@Preview(showBackground = true, name = "Bottom Bar")
@Composable
private fun BottomBarPreview() {
    AyatQuTheme(darkTheme = false) {
        var current by rememberSaveable { mutableStateOf(BottomNavDestination.HOME) }
        AyatQuBottomBar(current = current, onSelect = { current = it })
    }
}
