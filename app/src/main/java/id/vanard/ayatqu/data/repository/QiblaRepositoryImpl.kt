package id.vanard.ayatqu.data.repository

import id.vanard.ayatqu.data.remote.QiblaApiService
import id.vanard.ayatqu.data.remote.dto.QiblaDataDto
import id.vanard.ayatqu.domain.model.QiblaDirection
import id.vanard.ayatqu.domain.repository.QiblaRepository
import id.vanard.ayatqu.util.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class QiblaRepositoryImpl(
    private val api: QiblaApiService,
    private val networkUtils: NetworkUtils,
) : QiblaRepository {
    override suspend fun getQiblaDirection(
        latitude: Double,
        longitude: Double,
    ): Result<QiblaDirection> = withContext(Dispatchers.IO) {
        runCatching {
            if (!networkUtils.isNetworkAvailable()) {
                throw IOException("No internet connection. Please check your network and try again.")
            }
            val response = api.getQiblaDirection(latitude, longitude)
            check(response.success) { "The Qibla service could not calculate a direction." }
            response.data.toDomain()
        }
    }
}

internal fun QiblaDataDto.toDomain() = QiblaDirection(
    directionDegrees = qiblaDirection,
    compassBearing = compassBearing,
    latitude = location.latitude,
    longitude = location.longitude,
    distanceKm = distanceKm,
    distanceMiles = distanceMiles,
    note = note,
)
