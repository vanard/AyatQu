package id.vanard.ayatqu.navigation.routes

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface QuranRoute : NavKey {
    @Serializable
    data class Detail(
        val surahNumber: Int,
        val ayahNumber: Int? = null,
    ) : QuranRoute

    @Serializable
    data class JuzDetail(
        val juzNumber: Int,
    ) : QuranRoute
}
