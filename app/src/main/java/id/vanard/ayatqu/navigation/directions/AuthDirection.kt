package id.vanard.ayatqu.navigation.directions

import id.vanard.ayatqu.core.navigation.AppNavigationCommand
import id.vanard.ayatqu.navigation.routes.AuthRoute
import id.vanard.ayatqu.navigation.routes.MainRoute

object AuthDirection {
    val back = AppNavigationCommand.PopBack()
    val landing = AppNavigationCommand.ReplaceAll(AuthRoute.Landing)
    val login = AppNavigationCommand.Navigate(AuthRoute.Login)
    val signUp = AppNavigationCommand.Navigate(AuthRoute.SignUp)
    val home = AppNavigationCommand.ReplaceAll(MainRoute.Home)
}
