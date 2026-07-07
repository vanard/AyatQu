package id.vanard.ayatqu.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import id.vanard.ayatqu.domain.model.LastRead
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class LastReadPreference(private val context: Context) {

    private val dataStore = context.dataStore

    private val surahNumberKey = intPreferencesKey("last_read_surah_number")
    private val ayahNumberKey = intPreferencesKey("last_read_ayah_number")
    private val surahNameKey = stringPreferencesKey("last_read_surah_name")
    private val timestampKey = longPreferencesKey("last_read_timestamp")

    val lastRead: Flow<LastRead?> = dataStore.data.map { prefs ->
        val surah = prefs[surahNumberKey] ?: return@map null
        val ayah = prefs[ayahNumberKey] ?: return@map null
        val name = prefs[surahNameKey] ?: return@map null
        val time = prefs[timestampKey] ?: 0L
        LastRead(
            surahNumber = surah,
            ayahNumber = ayah,
            surahName = name,
            timestamp = time,
        )
    }

    suspend fun saveLastRead(lastRead: LastRead) {
        dataStore.edit { prefs ->
            prefs[surahNumberKey] = lastRead.surahNumber
            prefs[ayahNumberKey] = lastRead.ayahNumber
            prefs[surahNameKey] = lastRead.surahName
            prefs[timestampKey] = lastRead.timestamp
        }
    }

    suspend fun clearLastRead() {
        dataStore.edit { prefs ->
            prefs.remove(surahNumberKey)
            prefs.remove(ayahNumberKey)
            prefs.remove(surahNameKey)
            prefs.remove(timestampKey)
        }
    }
}
