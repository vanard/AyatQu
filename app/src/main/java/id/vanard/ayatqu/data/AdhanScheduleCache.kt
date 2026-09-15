package id.vanard.ayatqu.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import id.vanard.ayatqu.domain.model.AdhanDay
import kotlinx.coroutines.flow.first

class AdhanScheduleCache(context: Context) {
    private val store = context.dataStore
    private val key = stringPreferencesKey("adhan_schedule_v1")
    private val locationKey = stringPreferencesKey("adhan_schedule_location")
    private val gson = Gson()

    suspend fun read(location: String): List<AdhanDay> {
        val prefs = store.data.first()
        if (prefs[locationKey] != location) return emptyList()
        return runCatching {
            gson.fromJson(prefs[key], Array<AdhanDay>::class.java)?.toList().orEmpty()
        }.getOrDefault(emptyList())
    }

    suspend fun save(location: String, days: List<AdhanDay>) {
        store.edit {
            it[locationKey] = location
            it[key] = gson.toJson(days)
        }
    }
}
