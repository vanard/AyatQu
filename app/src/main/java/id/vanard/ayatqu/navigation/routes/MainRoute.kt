package id.vanard.ayatqu.navigation.routes

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface MainRoute : NavKey {
    @Serializable
    data object Home : MainRoute

    @Serializable
    data object Quran : MainRoute

    @Serializable
    data object Profile : MainRoute
}
