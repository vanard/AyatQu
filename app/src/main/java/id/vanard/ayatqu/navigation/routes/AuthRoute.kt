package id.vanard.ayatqu.navigation.routes

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface AuthRoute : NavKey {
    @Serializable
    data object Onboarding : AuthRoute

    @Serializable
    data object Landing : AuthRoute

    @Serializable
    data object Login : AuthRoute

    @Serializable
    data object SignUp : AuthRoute
}
