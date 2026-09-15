package id.vanard.ayatqu.presentation.qibla.contract

import id.vanard.ayatqu.domain.model.QiblaDirection
import kotlin.math.roundToInt

data class QiblaState(
    val isLoading: Boolean = false,
    val qibla: QiblaDirection? = null,
    val locationName: String? = null,
    val headingDegrees: Float? = null,
    val isCompassAvailable: Boolean? = null,
    val errorMessage: String? = null,
) {
    val angleToQibla: Float?
        get() {
            val direction = qibla?.directionDegrees?.toFloat() ?: return null
            val heading = headingDegrees ?: return null
            return (direction - heading + 360f) % 360f
        }

    /** Converts a true-north bearing onto a clockwise dial whose zero point is West. */
    val kaabaMarkerRotation: Float
        get() = ((qibla?.directionDegrees?.toFloat() ?: 0f) - WEST_BEARING + 360f) % 360f

    /** Shows the device's live true-north heading on the same West-up dial. */
    val deviceNeedleRotation: Float?
        get() = headingDegrees?.let { (it - WEST_BEARING + 360f) % 360f }

    val roundedDeviceHeading: Int?
        get() = headingDegrees?.roundToInt()?.mod(360)

    val turn: QiblaTurn?
        get() {
            val angle = angleToQibla ?: return null
            return when {
                angle <= ALIGNMENT_TOLERANCE || angle >= 360f - ALIGNMENT_TOLERANCE -> {
                    QiblaTurn.Aligned
                }
                angle <= 180f -> QiblaTurn.Right(angle.toInt())
                else -> QiblaTurn.Left((360f - angle).toInt())
            }
        }

    private companion object {
        const val ALIGNMENT_TOLERANCE = 3f
        const val WEST_BEARING = 270f
    }
}

sealed interface QiblaTurn {
    data class Left(val degrees: Int) : QiblaTurn
    data class Right(val degrees: Int) : QiblaTurn
    data object Aligned : QiblaTurn
}
