package id.vanard.ayatqu.domain.repository

import id.vanard.ayatqu.domain.model.PrayerTimesResult

interface PrayerTimeRepository {
    suspend fun getPrayerTimes(city: String, country: String): Result<PrayerTimesResult>
    suspend fun getPrayerTimesByCoordinates(
        latitude: Double,
        longitude: Double,
    ): Result<PrayerTimesResult>
}
