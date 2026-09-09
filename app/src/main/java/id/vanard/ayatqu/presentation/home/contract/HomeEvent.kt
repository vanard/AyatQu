package id.vanard.ayatqu.presentation.home.contract

sealed interface HomeEvent {
    data class LoadPrayerTimes(val fetchLocation: Boolean) : HomeEvent
    data object RetryClicked : HomeEvent
    data class LastReadClicked(val surahNumber: Int, val ayahNumber: Int) : HomeEvent
    data object QiblaClicked : HomeEvent
}

typealias OnHomeEvent = (HomeEvent) -> Unit
