package id.vanard.ayatqu.presentation.onboarding.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import id.vanard.ayatqu.navigation.routes.AuthRoute
import id.vanard.ayatqu.presentation.onboarding.OnboardingRouter

fun EntryProviderScope<NavKey>.onboarding() {
    entry<AuthRoute.Onboarding> { OnboardingRouter() }
}
