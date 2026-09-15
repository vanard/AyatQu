package id.vanard.ayatqu.data.repository

import id.vanard.ayatqu.data.PrayerTimeCache
import id.vanard.ayatqu.data.remote.PrayerTimeApiService
import id.vanard.ayatqu.data.remote.dto.PrayerTimingsDto
import id.vanard.ayatqu.domain.model.PrayerTime
import id.vanard.ayatqu.domain.model.PrayerTimesResult
import id.vanard.ayatqu.domain.repository.PrayerTimeRepository
import id.vanard.ayatqu.util.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import java.io.IOException

class PrayerTimeRepositoryImpl(
    private val api: PrayerTimeApiService,
    private val networkUtils: NetworkUtils,
    private val cache: PrayerTimeCache,
) : PrayerTimeRepository {

    override suspend fun getPrayerTimes(city: String, country: String): Result<PrayerTimesResult> =
        withContext(Dispatchers.IO) {
            runCatching {
                if (!networkUtils.isNetworkAvailable()) {
                    val cachedTimes = cache.cachedPrayerTimes.first()
                    if (!cachedTimes.isNullOrEmpty()) {
                        val cachedTimezone = cache.getCachedTimezone()
                        return@runCatching PrayerTimesResult(cachedTimes, cachedTimezone)
                    }
                    throw IOException("No internet connection and no cached prayer times available.")
                }

                val response = api.getPrayerTimesByCity(city = city, country = country)
                val prayerTimes = response.data.timings.toPrayerTimes()
                val timezone = response.data.meta?.timezone

                cache.savePrayerTimes(
                    prayerTimes = prayerTimes,
                    timezone = timezone,
                    city = city,
                    country = country,
                )

                PrayerTimesResult(prayerTimes, timezone)
            }
        }

    override suspend fun getPrayerTimesByCoordinates(
        latitude: Double,
        longitude: Double,
    ): Result<PrayerTimesResult> =
        withContext(Dispatchers.IO) {
            runCatching {
                if (!networkUtils.isNetworkAvailable()) {
                    val cachedTimes = cache.cachedPrayerTimes.first()
                    if (!cachedTimes.isNullOrEmpty()) {
                        val cachedTimezone = cache.getCachedTimezone()
                        return@runCatching PrayerTimesResult(cachedTimes, cachedTimezone)
                    }
                    throw IOException("No internet connection and no cached prayer times available.")
                }

                val response = api.getPrayerTimesByCoordinates(
                    latitude = latitude,
                    longitude = longitude,
                )
                val prayerTimes = response.data.timings.toPrayerTimes()
                val timezone = response.data.meta?.timezone

                cache.savePrayerTimes(
                    prayerTimes = prayerTimes,
                    latitude = latitude,
                    longitude = longitude,
                    timezone = timezone,
                )

                PrayerTimesResult(prayerTimes, timezone)
            }
        }

}

internal fun PrayerTimingsDto.toPrayerTimes(): List<PrayerTime> = buildList {
    imsak
        ?.cleanTime()
        ?.takeIf(String::isNotBlank)
        ?.let { add(PrayerTime(name = "Imsak", time = it)) }
    add(PrayerTime(name = "Fajr", time = fajr.cleanTime()))
    add(PrayerTime(name = "Sunrise", time = sunrise.cleanTime()))
    add(PrayerTime(name = "Dhuhr", time = dhuhr.cleanTime()))
    add(PrayerTime(name = "Asr", time = asr.cleanTime()))
    add(PrayerTime(name = "Maghrib", time = maghrib.cleanTime()))
    add(PrayerTime(name = "Isha", time = isha.cleanTime()))
}

/**
 * Strips timezone suffix like " (WIB)" or " +07" from the time string.
 * E.g. "04:32 (WIB)" → "04:32"
 */
private fun String.cleanTime(): String = substringBefore(" ").trim()
