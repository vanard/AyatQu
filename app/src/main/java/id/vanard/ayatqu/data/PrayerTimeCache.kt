package id.vanard.ayatqu.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import id.vanard.ayatqu.domain.model.PrayerTime
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class PrayerTimeCache(context: Context) {
    private val dataStore = context.dataStore

    private val prayerTimesKey = stringPreferencesKey("prayer_times_json")
    private val lastFetchDateKey = stringPreferencesKey("prayer_times_date")
    private val lastFetchTimestampKey = longPreferencesKey("prayer_times_timestamp")
    private val latitudeKey = stringPreferencesKey("prayer_times_latitude")
    private val longitudeKey = stringPreferencesKey("prayer_times_longitude")
    private val timezoneKey = stringPreferencesKey("prayer_times_timezone")
    private val cityKey = stringPreferencesKey("prayer_times_city")
    private val countryKey = stringPreferencesKey("prayer_times_country")

    val cachedPrayerTimes: Flow<List<PrayerTime>?> = dataStore.data.map { prefs ->
        val json = prefs[prayerTimesKey] ?: return@map null
        parsePrayerTimesJson(json)
    }

    suspend fun savePrayerTimes(
        prayerTimes: List<PrayerTime>,
        latitude: Double? = null,
        longitude: Double? = null,
        timezone: String? = null,
        city: String? = null,
        country: String? = null,
    ) {
        val today = java.time.LocalDate.now(resolveZone(timezone ?: getCachedTimezone())).toString()
        val timestamp = System.currentTimeMillis()
        val json = serializePrayerTimesJson(prayerTimes)

        dataStore.edit { prefs ->
            prefs[prayerTimesKey] = json
            prefs[lastFetchDateKey] = today
            prefs[lastFetchTimestampKey] = timestamp
            latitude?.let { prefs[latitudeKey] = it.toString() }
            longitude?.let { prefs[longitudeKey] = it.toString() }
            timezone?.let { prefs[timezoneKey] = it }
            if (city != null && country != null) {
                prefs[cityKey] = city
                prefs[countryKey] = country
                prefs.remove(latitudeKey)
                prefs.remove(longitudeKey)
            } else if (latitude != null && longitude != null) {
                prefs.remove(cityKey)
                prefs.remove(countryKey)
            }
        }
    }

    suspend fun isCacheValid(): Boolean {
        val today = java.time.LocalDate.now(resolveZone(getCachedTimezone())).toString()
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

    suspend fun getCachedLocation(): Pair<Double, Double>? {
        return dataStore.data.map { prefs ->
            val lat = prefs[latitudeKey]?.toDoubleOrNull()
            val lng = prefs[longitudeKey]?.toDoubleOrNull()
            if (lat != null && lng != null) Pair(lat, lng) else null
        }.first()
    }

    suspend fun getCachedTimezone(): String? {
        return dataStore.data.map { prefs ->
            prefs[timezoneKey]
        }.first()
    }

    suspend fun getCachedCity(): Pair<String, String>? {
        val prefs = dataStore.data.first()
        val city = prefs[cityKey] ?: return null
        return city to (prefs[countryKey] ?: return null)
    }

    suspend fun clearCache() {
        dataStore.edit { prefs ->
            prefs.remove(prayerTimesKey)
            prefs.remove(lastFetchDateKey)
            prefs.remove(lastFetchTimestampKey)
            // Retain the scheduling location so clearing content does not disable reminders.
        }
    }

    private fun resolveZone(timezone: String?) = runCatching {
        java.time.ZoneId.of(timezone)
    }.getOrDefault(java.time.ZoneId.systemDefault())

    private fun serializePrayerTimesJson(prayerTimes: List<PrayerTime>): String {
        return prayerTimes.joinToString(";") { "${it.name}:${it.time}" }
    }

    private fun parsePrayerTimesJson(json: String): List<PrayerTime> {
        return decodePrayerTimes(json)
    }
}

internal fun decodePrayerTimes(value: String): List<PrayerTime> = value.split(";").mapNotNull { entry ->
    val parts = entry.split(":", limit = 2)
    if (parts.size != 2 || parts[0].isBlank() ||
        runCatching { java.time.LocalTime.parse(parts[1]) }.isFailure) null
    else PrayerTime(name = parts[0], time = parts[1])
}
