package id.vanard.ayatqu.presentation.quran.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import id.vanard.ayatqu.navigation.routes.MainRoute
import id.vanard.ayatqu.navigation.routes.QuranRoute
import id.vanard.ayatqu.presentation.quran.detail.DetailSurahRouter
import id.vanard.ayatqu.presentation.quran.list.QuranRouter

fun EntryProviderScope<NavKey>.quran() {
    entry<MainRoute.Quran> { QuranRouter() }
    entry<QuranRoute.Detail> { route ->
        DetailSurahRouter(
            surahNumber = route.surahNumber,
            ayahNumber = route.ayahNumber,
        )
    }
}
