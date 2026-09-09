package id.vanard.ayatqu.presentation.home.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import id.vanard.ayatqu.navigation.routes.MainRoute
import id.vanard.ayatqu.presentation.home.HomeRouter

fun EntryProviderScope<NavKey>.home() {
    entry<MainRoute.Home> { HomeRouter() }
}
