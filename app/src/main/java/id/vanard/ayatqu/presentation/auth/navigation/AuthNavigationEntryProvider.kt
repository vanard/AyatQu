package id.vanard.ayatqu.presentation.auth.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import id.vanard.ayatqu.navigation.routes.AuthRoute
import id.vanard.ayatqu.presentation.auth.landing.LandingRouter
import id.vanard.ayatqu.presentation.auth.login.LoginRouter
import id.vanard.ayatqu.presentation.auth.signup.SignUpRouter

fun EntryProviderScope<NavKey>.auth() {
    entry<AuthRoute.Landing> { LandingRouter() }
    entry<AuthRoute.Login> { LoginRouter() }
    entry<AuthRoute.SignUp> { SignUpRouter() }
}
