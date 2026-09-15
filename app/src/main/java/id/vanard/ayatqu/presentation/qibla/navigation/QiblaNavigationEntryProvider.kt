package id.vanard.ayatqu.presentation.qibla.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import id.vanard.ayatqu.navigation.routes.QiblaRoute
import id.vanard.ayatqu.presentation.qibla.QiblaRouter

fun EntryProviderScope<NavKey>.qibla() {
    entry<QiblaRoute.Root> { QiblaRouter() }
}
