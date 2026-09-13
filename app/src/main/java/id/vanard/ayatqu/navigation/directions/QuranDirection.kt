package id.vanard.ayatqu.navigation.directions

import id.vanard.ayatqu.core.navigation.AppNavigationCommand
import id.vanard.ayatqu.navigation.routes.QuranRoute

object QuranDirection {
    val back = AppNavigationCommand.PopBack()

    fun detail(
        surahNumber: Int,
        ayahNumber: Int? = null,
    ) = AppNavigationCommand.Navigate(
        QuranRoute.Detail(
            surahNumber = surahNumber,
            ayahNumber = ayahNumber,
        )
    )

    fun juz(juzNumber: Int) = AppNavigationCommand.Navigate(
        QuranRoute.JuzDetail(juzNumber = juzNumber)
    )
}
