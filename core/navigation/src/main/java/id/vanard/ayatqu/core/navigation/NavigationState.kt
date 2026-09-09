package id.vanard.ayatqu.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator

@Composable
fun rememberNavigationState(
    initialRoute: NavKey,
    startTopLevelRoute: NavKey,
    topLevelRoutes: Set<NavKey>,
): NavigationState {
    require(startTopLevelRoute in topLevelRoutes)

    val rootBackStack = rememberNavBackStack(initialRoute)
    val topLevelRouteList = topLevelRoutes.toList()
    val backStacks = topLevelRouteList.associateWith { route -> rememberNavBackStack(route) }
    val initialTopLevelRoute = initialRoute.takeIf { it in topLevelRoutes } ?: startTopLevelRoute
    val topLevelRouteIndex = rememberSaveable {
        mutableIntStateOf(topLevelRouteList.indexOf(initialTopLevelRoute))
    }
    val isMainFlow = rememberSaveable { mutableStateOf(initialRoute in topLevelRoutes) }

    return remember(initialRoute, startTopLevelRoute, topLevelRoutes) {
        NavigationState(
            rootBackStack = rootBackStack,
            startTopLevelRoute = startTopLevelRoute,
            topLevelRoutes = topLevelRouteList,
            topLevelRouteIndexState = topLevelRouteIndex,
            topLevelBackStacks = backStacks,
            isMainFlowState = isMainFlow,
        )
    }
}

class NavigationState internal constructor(
    private val rootBackStack: NavBackStack<NavKey>,
    val startTopLevelRoute: NavKey,
    private val topLevelRoutes: List<NavKey>,
    private val topLevelRouteIndexState: MutableIntState,
    val topLevelBackStacks: Map<NavKey, NavBackStack<NavKey>>,
    private val isMainFlowState: MutableState<Boolean>,
) {
    var topLevelRoute: NavKey
        get() = topLevelRoutes[topLevelRouteIndexState.intValue]
        private set(value) {
            topLevelRouteIndexState.intValue = topLevelRoutes.indexOf(value)
                .takeIf { it >= 0 }
                ?: error("Unknown top-level route: $value")
        }
    val isMainFlow: Boolean get() = isMainFlowState.value
    val currentRoute: NavKey?
        get() = currentBackStack().lastOrNull()

    fun navigate(route: NavKey, options: NavOptions = NavOptions()) {
        if (route in topLevelBackStacks) {
            isMainFlowState.value = true
            if (topLevelRoute == route) {
                val stack = checkNotNull(topLevelBackStacks[route])
                while (stack.size > 1) stack.removeLastOrNull()
            }
            topLevelRoute = route
            return
        }

        val stack = currentBackStack()
        options.popUpToRoute?.let { target ->
            popBackTo(stack = stack, route = target, inclusive = options.inclusive)
        }
        if (!options.singleTop || stack.lastOrNull() != route) {
            stack.add(route)
        }
    }

    fun popBack(): Boolean {
        val stack = currentBackStack()
        if (stack.size > 1) {
            stack.removeLastOrNull()
            return true
        }
        if (isMainFlow && topLevelRoute != startTopLevelRoute) {
            topLevelRoute = startTopLevelRoute
            return true
        }
        return false
    }

    fun popBackTo(route: NavKey, inclusive: Boolean = false) {
        popBackTo(stack = currentBackStack(), route = route, inclusive = inclusive)
    }

    fun replaceAll(route: NavKey) {
        if (route in topLevelBackStacks) {
            topLevelBackStacks.values.forEach { stack ->
                while (stack.size > 1) stack.removeLastOrNull()
            }
            topLevelRoute = route
            isMainFlowState.value = true
        } else {
            rootBackStack.clear()
            rootBackStack.add(route)
            isMainFlowState.value = false
        }
    }

    @Composable
    fun toDecoratedEntries(
        entryProvider: (NavKey) -> NavEntry<NavKey>,
    ): List<NavEntry<NavKey>> {
        val rootEntries = rememberDecoratedNavEntries(
            backStack = rootBackStack,
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(),
            ),
            entryProvider = entryProvider,
        )
        val topLevelEntries = topLevelBackStacks.mapValues { (_, stack) ->
            rememberDecoratedNavEntries(
                backStack = stack,
                entryDecorators = listOf(
                    rememberSaveableStateHolderNavEntryDecorator(),
                    rememberViewModelStoreNavEntryDecorator(),
                ),
                entryProvider = entryProvider,
            )
        }

        if (!isMainFlow) return rootEntries
        return if (topLevelRoute == startTopLevelRoute) {
            topLevelEntries[startTopLevelRoute].orEmpty()
        } else {
            topLevelEntries[startTopLevelRoute].orEmpty() + topLevelEntries[topLevelRoute].orEmpty()
        }
    }

    private fun currentBackStack(): NavBackStack<NavKey> =
        if (isMainFlow) {
            checkNotNull(topLevelBackStacks[topLevelRoute])
        } else {
            rootBackStack
        }

    private fun popBackTo(
        stack: NavBackStack<NavKey>,
        route: NavKey,
        inclusive: Boolean,
    ) {
        val targetIndex = stack.indexOfLast { it == route }
        if (targetIndex < 0) return
        val keepCount = if (inclusive) targetIndex else targetIndex + 1
        while (stack.size > keepCount) stack.removeLastOrNull()
    }
}
