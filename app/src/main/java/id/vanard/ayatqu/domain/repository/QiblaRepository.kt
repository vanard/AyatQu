package id.vanard.ayatqu.domain.repository

import id.vanard.ayatqu.domain.model.QiblaDirection

interface QiblaRepository {
    suspend fun getQiblaDirection(latitude: Double, longitude: Double): Result<QiblaDirection>
}
