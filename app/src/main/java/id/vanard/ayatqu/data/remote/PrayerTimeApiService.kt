package id.vanard.ayatqu.data.remote

import id.vanard.ayatqu.data.remote.dto.PrayerTimeResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PrayerTimeApiService {

    /**
     * Get prayer times by city name.
     * Uses the Aladhan API (free, no key required).
     *
     * @param date Date in DD-MM-YYYY format, or "today" for current day
     * @param city City name
     * @param country Country name or code
     * @param method Calculation method (e.g., 3 = Muslim World League, 4 = Umm Al-Qura)
     */
    @GET("timingsByCity/{date}")
    suspend fun getPrayerTimesByCity(
        @Path("date") date: String = "today",
        @Query("city") city: String,
        @Query("country") country: String,
        @Query("method") method: Int = 3, // Muslim World League
    ): PrayerTimeResponse

    /**
     * Get prayer times by coordinates.
     *
     * @param date Date in DD-MM-YYYY format, or "today" for current day
     * @param latitude Latitude
     * @param longitude Longitude
     * @param method Calculation method
     */
    @GET("timings/{date}")
    suspend fun getPrayerTimesByCoordinates(
        @Path("date") date: String = "today",
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("method") method: Int = 3,
    ): PrayerTimeResponse
}
