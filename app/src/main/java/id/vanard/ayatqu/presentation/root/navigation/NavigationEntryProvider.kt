package id.vanard.ayatqu.presentation.root.navigation

import androidx.navigation3.runtime.entryProvider
import id.vanard.ayatqu.presentation.auth.navigation.auth
import id.vanard.ayatqu.presentation.home.navigation.home
import id.vanard.ayatqu.presentation.onboarding.navigation.onboarding
import id.vanard.ayatqu.presentation.profile.navigation.profile
import id.vanard.ayatqu.presentation.quran.navigation.quran

val ayatQuEntryProvider = entryProvider {
    onboarding()
    auth()
    home()
    quran()
    profile()
}
