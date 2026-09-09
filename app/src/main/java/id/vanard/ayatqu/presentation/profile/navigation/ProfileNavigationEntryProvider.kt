package id.vanard.ayatqu.presentation.profile.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import id.vanard.ayatqu.navigation.routes.MainRoute
import id.vanard.ayatqu.presentation.profile.ProfileRouter

fun EntryProviderScope<NavKey>.profile() {
    entry<MainRoute.Profile> { ProfileRouter() }
}
