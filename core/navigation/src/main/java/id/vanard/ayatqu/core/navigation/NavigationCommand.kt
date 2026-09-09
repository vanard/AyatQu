package id.vanard.ayatqu.core.navigation

import androidx.navigation3.runtime.NavKey

sealed interface AppNavigationCommand {
    data class Navigate(
        val route: NavKey,
        val options: NavOptions = NavOptions(),
    ) : AppNavigationCommand

    data class PopBack(
        val result: Any? = null,
    ) : AppNavigationCommand

    data class PopBackTo(
        val route: NavKey,
        val inclusive: Boolean = false,
    ) : AppNavigationCommand

    data class ReplaceAll(
        val route: NavKey,
    ) : AppNavigationCommand
}

data class NavOptions(
    val popUpToRoute: NavKey? = null,
    val inclusive: Boolean = false,
    val singleTop: Boolean = true,
)
