package id.vanard.ayatqu.data.repository

import id.vanard.ayatqu.data.PrayerTimeCache
import id.vanard.ayatqu.data.remote.PrayerTimeApiService
import id.vanard.ayatqu.data.remote.dto.PrayerTimingsDto
import id.vanard.ayatqu.domain.model.PrayerTime
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

    override suspend fun getPrayerTimes(city: String, country: String): Result<List<PrayerTime>> =
        withContext(Dispatchers.IO) {
            runCatching {
                // Check cache first - offline first approach
                val cachedTimes = cache.cachedPrayerTimes.first()
                val isCacheValid = cache.isCacheValid()

                // Return cached data if valid (fetched today)
                if (isCacheValid && !cachedTimes.isNullOrEmpty()) {
                    return@runCatching cachedTimes
                }

                // Cache is stale or empty, fetch from API
                if (!networkUtils.isNetworkAvailable()) {
                    // If offline and no cache, throw error
                    if (cachedTimes.isNullOrEmpty()) {
                        throw IOException("No internet connection and no cached prayer times available.")
                    }
                    // If offline but have old cache, return it
                    return@runCatching cachedTimes
                }

                // Fetch from API
                val response = api.getPrayerTimesByCity(city = city, country = country)
                val prayerTimes = response.data.timings.toPrayerTimes()

                // Save to cache with city label
                cache.savePrayerTimes(
                    prayerTimes = prayerTimes,
                    locationLabel = city,
                )

                prayerTimes
            }
        }

    override suspend fun getPrayerTimesByCoordinates(
        latitude: Double,
        longitude: Double,
    ): Result<List<PrayerTime>> =
        withContext(Dispatchers.IO) {
            runCatching {
                // Check cache first - offline first approach
                val cachedTimes = cache.cachedPrayerTimes.first()
                val isCacheValid = cache.isCacheValid()

                // Return cached data if valid (fetched today)
                if (isCacheValid && !cachedTimes.isNullOrEmpty()) {
                    return@runCatching cachedTimes
                }

                // Cache is stale or empty, fetch from API
                if (!networkUtils.isNetworkAvailable()) {
                    // If offline and no cache, throw error
                    if (cachedTimes.isNullOrEmpty()) {
                        throw IOException("No internet connection and no cached prayer times available.")
                    }
                    // If offline but have old cache, return it
                    return@runCatching cachedTimes
                }

                // Fetch from API
                val response = api.getPrayerTimesByCoordinates(
                    latitude = latitude,
                    longitude = longitude,
                )
                val prayerTimes = response.data.timings.toPrayerTimes()

                // Save to cache with location data
                cache.savePrayerTimes(
                    prayerTimes = prayerTimes,
                    latitude = latitude,
                    longitude = longitude,
                    locationLabel = "%.2f, %.2f".format(latitude, longitude),
                )

                prayerTimes
            }
        }

    private fun PrayerTimingsDto.toPrayerTimes(): List<PrayerTime> = listOf(
        PrayerTime(name = "Fajr", time = fajr.cleanTime()),
        PrayerTime(name = "Sunrise", time = sunrise.cleanTime()),
        PrayerTime(name = "Dhuhr", time = dhuhr.cleanTime()),
        PrayerTime(name = "Asr", time = asr.cleanTime()),
        PrayerTime(name = "Maghrib", time = maghrib.cleanTime()),
        PrayerTime(name = "Isha", time = isha.cleanTime()),
    )

    /**
     * Strips timezone suffix like " (WIB)" or " +07" from the time string.
     * E.g. "04:32 (WIB)" → "04:32"
     */
    private fun String.cleanTime(): String = substringBefore(" ").trim()
}
