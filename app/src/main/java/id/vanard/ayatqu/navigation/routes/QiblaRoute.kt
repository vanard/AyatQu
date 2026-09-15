package id.vanard.ayatqu.navigation.routes

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface QiblaRoute : NavKey {
    @Serializable
    data object Root : QiblaRoute
}
