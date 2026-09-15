package id.vanard.ayatqu.data.remote.dto

import com.google.gson.annotations.SerializedName

data class QiblaResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("service") val service: String,
    @SerializedName("data") val data: QiblaDataDto,
    @SerializedName("timestamp") val timestamp: String,
)

data class QiblaDataDto(
    @SerializedName("qibla_direction") val qiblaDirection: Double,
    @SerializedName("compass_bearing") val compassBearing: String,
    @SerializedName("location") val location: CoordinatesDto,
    @SerializedName("kaaba_coordinates") val kaabaCoordinates: CoordinatesDto,
    @SerializedName("distance_km") val distanceKm: Double,
    @SerializedName("distance_miles") val distanceMiles: Double,
    @SerializedName("note") val note: String,
)

data class CoordinatesDto(
    @SerializedName("latitude") val latitude: Double,
    @SerializedName("longitude") val longitude: Double,
)
