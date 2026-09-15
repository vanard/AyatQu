package id.vanard.ayatqu.navigation.directions

import id.vanard.ayatqu.core.navigation.AppNavigationCommand
import id.vanard.ayatqu.navigation.routes.QiblaRoute

object QiblaDirection {
    val root = AppNavigationCommand.Navigate(QiblaRoute.Root)
    val back = AppNavigationCommand.PopBack()
}
