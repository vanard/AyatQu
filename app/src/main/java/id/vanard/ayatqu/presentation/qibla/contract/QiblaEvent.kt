package id.vanard.ayatqu.presentation.qibla.contract

sealed interface QiblaEvent {
    data class InitialData(val fetchLocation: Boolean) : QiblaEvent
    data class HeadingChanged(val degrees: Float) : QiblaEvent
    data class CompassAvailabilityChanged(val isAvailable: Boolean) : QiblaEvent
    data object RetryClicked : QiblaEvent
    data object BackClicked : QiblaEvent
}

typealias OnQiblaEvent = (QiblaEvent) -> Unit
