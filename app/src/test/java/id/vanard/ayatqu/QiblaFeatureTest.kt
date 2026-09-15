package id.vanard.ayatqu

import com.google.gson.Gson
import id.vanard.ayatqu.core.navigation.AppNavigationCommand
import id.vanard.ayatqu.data.remote.dto.QiblaResponse
import id.vanard.ayatqu.data.repository.toDomain
import id.vanard.ayatqu.domain.model.QiblaDirection
import id.vanard.ayatqu.navigation.directions.QiblaDirection as QiblaNavigationDirection
import id.vanard.ayatqu.navigation.routes.QiblaRoute
import id.vanard.ayatqu.presentation.qibla.contract.QiblaState
import id.vanard.ayatqu.presentation.qibla.contract.QiblaTurn
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class QiblaFeatureTest {
    @Test
    fun parsesAndMapsQiblaResponse() {
        val response = Gson().fromJson(
            """
            {
              "success": true,
              "service": "qibla",
              "data": {
                "qibla_direction": 58.48,
                "compass_bearing": "ENE",
                "location": { "latitude": 40.7128, "longitude": -74.006 },
                "kaaba_coordinates": { "latitude": 21.4225, "longitude": 39.8262 },
                "distance_km": 10306.31,
                "distance_miles": 6404.04,
                "note": "Bearing is calculated as true north."
              },
              "timestamp": "2026-09-14T15:02:40.897Z"
            }
            """.trimIndent(),
            QiblaResponse::class.java,
        )

        val direction = response.data.toDomain()

        assertEquals(58.48, direction.directionDegrees, 0.001)
        assertEquals("ENE", direction.compassBearing)
        assertEquals(40.7128, direction.latitude, 0.0001)
        assertEquals(-74.006, direction.longitude, 0.0001)
        assertEquals(10306.31, direction.distanceKm, 0.001)
    }

    @Test
    fun needleRotationIsRelativeToCurrentTrueNorthHeading() {
        val state = QiblaState(
            qibla = sampleDirection(directionDegrees = 58.48),
            headingDegrees = 20f,
        )

        assertEquals(38.48f, state.angleToQibla ?: 0f, 0.001f)
        assertEquals(QiblaTurn.Right(38), state.turn)
    }

    @Test
    fun needleRotationWrapsAroundNorth() {
        val state = QiblaState(
            qibla = sampleDirection(directionDegrees = 10.0),
            headingDegrees = 350f,
        )

        assertEquals(20f, state.angleToQibla ?: 0f, 0.001f)
        assertEquals(QiblaTurn.Right(20), state.turn)
    }

    @Test
    fun turnInstructionUsesShortestDirection() {
        val state = QiblaState(
            qibla = sampleDirection(directionDegrees = 40.0),
            headingDegrees = 175f,
        )

        assertEquals(225f, state.angleToQibla ?: 0f, 0.001f)
        assertEquals(QiblaTurn.Left(135), state.turn)
    }

    @Test
    fun westUpDialMapsApiBearingAndDeviceHeadingToTheSameCoordinateSystem() {
        val state = QiblaState(
            qibla = sampleDirection(directionDegrees = 257.0),
            headingDegrees = 175f,
        )

        assertEquals(347f, state.kaabaMarkerRotation, 0.001f)
        assertEquals(265f, state.deviceNeedleRotation ?: 0f, 0.001f)
    }

    @Test
    fun needleMeetsKaabaMarkerWhenDeviceFacesQibla() {
        val state = QiblaState(
            qibla = sampleDirection(directionDegrees = 292.1),
            headingDegrees = 292.1f,
        )

        assertEquals(state.kaabaMarkerRotation, state.deviceNeedleRotation ?: 0f, 0.001f)
        assertEquals(QiblaTurn.Aligned, state.turn)
    }

    @Test
    fun clockwiseWestUpDialPlacesWestNorthWestBeforeNorth() {
        val state = QiblaState(qibla = sampleDirection(directionDegrees = 295.1))

        assertEquals(25.1f, state.kaabaMarkerRotation, 0.001f)
    }

    @Test
    fun qiblaDirectionUsesTypedRoute() {
        val command: AppNavigationCommand = QiblaNavigationDirection.root

        assertTrue(command is AppNavigationCommand.Navigate)
        assertEquals(QiblaRoute.Root, (command as AppNavigationCommand.Navigate).route)
    }

    private fun sampleDirection(directionDegrees: Double) = QiblaDirection(
        directionDegrees = directionDegrees,
        compassBearing = "ENE",
        latitude = 40.7128,
        longitude = -74.006,
        distanceKm = 10306.31,
        distanceMiles = 6404.04,
        note = "Bearing is calculated as true north.",
    )
}
