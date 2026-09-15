package id.vanard.ayatqu.domain.model

data class QiblaDirection(
    val directionDegrees: Double,
    val compassBearing: String,
    val latitude: Double,
    val longitude: Double,
    val distanceKm: Double,
    val distanceMiles: Double,
    val note: String,
)
