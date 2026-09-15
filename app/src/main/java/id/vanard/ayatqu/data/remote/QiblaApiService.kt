package id.vanard.ayatqu.data.remote

import id.vanard.ayatqu.data.remote.dto.QiblaResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface QiblaApiService {
    @GET("qibla")
    suspend fun getQiblaDirection(
        @Query("lat") latitude: Double,
        @Query("lng") longitude: Double,
    ): QiblaResponse
}
