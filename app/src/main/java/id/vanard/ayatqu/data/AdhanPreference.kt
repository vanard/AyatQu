package id.vanard.ayatqu.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import id.vanard.ayatqu.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AdhanPreference(private val context: Context) {

    private val dataStore = context.dataStore

    private val notificationsEnabledKey = stringPreferencesKey("adhan_notifications_enabled")
    private val adhanSoundTypeKey = stringPreferencesKey("adhan_sound_type")

    val notificationsEnabled: Flow<Boolean> = dataStore.data
        .map { prefs -> prefs[notificationsEnabledKey] != "false" }

    val adhanSoundType: Flow<String> = dataStore.data
        .map { prefs -> normalizeSoundType(prefs[adhanSoundTypeKey]) }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[notificationsEnabledKey] = if (enabled) "true" else "false"
        }
    }

    suspend fun setAdhanSoundType(type: String) {
        dataStore.edit { prefs ->
            prefs[adhanSoundTypeKey] = normalizeSoundType(type)
        }
    }

    companion object {
        const val SOUND_TYPE_SUBUH = "adzan_subuh"
        const val SOUND_TYPE_OMAR_HISHAM = "adhan_omar_hisham"
        const val SOUND_TYPE_DEFAULT = SOUND_TYPE_OMAR_HISHAM
        const val SOUND_TYPE_MAGHRIB_MTA = "adhan_maghrib_mta"
        const val SOUND_TYPE_SILENT = "silent"
        val soundTypes = listOf(SOUND_TYPE_DEFAULT, SOUND_TYPE_SUBUH,
            SOUND_TYPE_MAGHRIB_MTA, SOUND_TYPE_SILENT)

        // Saved values for the removed default recording resolve to Omar Hisham too.
        fun normalizeSoundType(type: String?): String =
            type?.takeIf { it in soundTypes } ?: SOUND_TYPE_DEFAULT

        fun getRawResId(soundType: String): Int {
            return when (soundType) {
                SOUND_TYPE_SUBUH -> R.raw.adzan_subuh
                SOUND_TYPE_OMAR_HISHAM -> R.raw.adhan_omar_hisham
                SOUND_TYPE_MAGHRIB_MTA -> R.raw.adhan_maghrib_mta
                else -> R.raw.adhan_omar_hisham
            }
        }

        fun getDisplayName(soundType: String): String {
            return when (soundType) {
                SOUND_TYPE_SUBUH -> "Subuh - Ust. Ruhani"
                SOUND_TYPE_OMAR_HISHAM -> "Omar Hisham Al Arabi"
                SOUND_TYPE_MAGHRIB_MTA -> "Maghrib - MTA"
                SOUND_TYPE_SILENT -> "Silent"
                else -> "Omar Hisham Al Arabi"
            }
        }
    }
}
