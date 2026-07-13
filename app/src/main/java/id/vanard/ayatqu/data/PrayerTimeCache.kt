package id.vanard.ayatqu.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import id.vanard.ayatqu.domain.model.PrayerTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PrayerTimeCache(context: Context) {
    private val dataStore = context.dataStore

    private val prayerTimesKey = stringPreferencesKey("prayer_times_json")
    private val lastFetchDateKey = stringPreferencesKey("prayer_times_date")
    private val lastFetchTimestampKey = longPreferencesKey("prayer_times_timestamp")
    private val latitudeKey = stringPreferencesKey("prayer_times_latitude")
    private val longitudeKey = stringPreferencesKey("prayer_times_longitude")
    private val locationLabelKey = stringPreferencesKey("prayer_times_location_label")

    val cachedPrayerTimes: Flow<List<PrayerTime>?> = dataStore.data.map { prefs ->
        val json = prefs[prayerTimesKey] ?: return@map null
        parsePrayerTimesJson(json)
    }

    suspend fun savePrayerTimes(
        prayerTimes: List<PrayerTime>,
        latitude: Double? = null,
        longitude: Double? = null,
        locationLabel: String? = null,
    ) {
        val today = getCurrentDate()
        val timestamp = System.currentTimeMillis()
        val json = serializePrayerTimesJson(prayerTimes)

        dataStore.edit { prefs ->
            prefs[prayerTimesKey] = json
            prefs[lastFetchDateKey] = today
            prefs[lastFetchTimestampKey] = timestamp
            latitude?.let { prefs[latitudeKey] = it.toString() }
            longitude?.let { prefs[longitudeKey] = it.toString() }
            locationLabel?.let { prefs[locationLabelKey] = it }
        }
    }

    suspend fun isCacheValid(): Boolean {
        val today = getCurrentDate()
        return dataStore.data.map { prefs ->
            val cachedDate = prefs[lastFetchDateKey]
            cachedDate == today
        }.first()
    }

    suspend fun getLastFetchDate(): String? {
        return dataStore.data.map { prefs ->
            prefs[lastFetchDateKey]
        }.first()
    }

    suspend fun getCachedLocation(): Triple<Double?, Double?, String?>? {
        return dataStore.data.map { prefs ->
            val lat = prefs[latitudeKey]?.toDoubleOrNull()
            val lng = prefs[longitudeKey]?.toDoubleOrNull()
            val label = prefs[locationLabelKey]
            if (lat != null && lng != null) Triple(lat, lng, label) else null
        }.first()
    }

    suspend fun clearCache() {
        dataStore.edit { prefs ->
            prefs.remove(prayerTimesKey)
            prefs.remove(lastFetchDateKey)
            prefs.remove(lastFetchTimestampKey)
            prefs.remove(latitudeKey)
            prefs.remove(longitudeKey)
            prefs.remove(locationLabelKey)
        }
    }

    private fun getCurrentDate(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun serializePrayerTimesJson(prayerTimes: List<PrayerTime>): String {
        return prayerTimes.joinToString(";") { "${it.name}:${it.time}" }
    }

    private fun parsePrayerTimesJson(json: String): List<PrayerTime> {
        if (json.isBlank()) return emptyList()
        return json.split(";").mapNotNull { entry ->
            val parts = entry.split(":")
            if (parts.size == 2) {
                PrayerTime(name = parts[0], time = parts[1])
            } else null
        }
    }
}
