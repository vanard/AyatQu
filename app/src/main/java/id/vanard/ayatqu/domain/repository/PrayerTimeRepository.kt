package id.vanard.ayatqu.domain.repository

import id.vanard.ayatqu.domain.model.PrayerTime

interface PrayerTimeRepository {
    suspend fun getPrayerTimes(city: String, country: String): Result<List<PrayerTime>>
    suspend fun getPrayerTimesByCoordinates(latitude: Double, longitude: Double): Result<List<PrayerTime>>
}
