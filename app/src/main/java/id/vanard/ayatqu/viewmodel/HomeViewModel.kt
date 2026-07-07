package id.vanard.ayatqu.viewmodel

import androidx.lifecycle.viewModelScope
import id.vanard.ayatqu.domain.model.LastRead
import id.vanard.ayatqu.domain.model.PrayerTime
import id.vanard.ayatqu.domain.repository.PrayerTimeRepository
import id.vanard.ayatqu.domain.repository.QuranRepository
import id.vanard.ayatqu.util.LocationHelper
import id.vanard.ayatqu.util.NetworkUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ── UI State ──────────────────────────────────────────────────────────────────

data class HomeUiState(
    val lastRead: LastRead? = null,
    val prayerTimes: List<PrayerTime> = emptyList(),
    val isPrayerTimesLoading: Boolean = false,
    val prayerTimesError: String? = null,
    val locationLabel: String = "Your Location",
)

// ── ViewModel ─────────────────────────────────────────────────────────────────

class HomeViewModel(
    private val quranRepository: QuranRepository,
    private val prayerTimeRepository: PrayerTimeRepository,
    private val locationHelper: LocationHelper,
    networkUtils: NetworkUtils,
) : NetworkAwareViewModel(networkUtils) {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeLastRead()
    }

    private fun observeLastRead() {
        viewModelScope.launch {
            quranRepository.getLastRead().collect { lastRead ->
                val defaultLastRead = LastRead(
                    surahNumber = 1,
                    ayahNumber = 1,
                    surahName = "Al-Fatihah",
                )
                _uiState.update { it.copy(lastRead = lastRead ?: defaultLastRead) }
            }
        }
    }

    /**
     * Called after location permission is granted.
     * Fetches the user's location once and loads prayer times by coordinates.
     * Falls back to last-known location, then to the default city if unavailable.
     */
    fun loadPrayerTimesWithLocation() {
        viewModelScope.launch {
            _uiState.update { it.copy(isPrayerTimesLoading = true, prayerTimesError = null) }

            val location = locationHelper.getCurrentLocation()
                ?: locationHelper.getLastKnownLocation()

            if (location != null) {
                prayerTimeRepository.getPrayerTimesByCoordinates(
                    latitude = location.latitude,
                    longitude = location.longitude,
                ).onSuccess { times ->
                    _uiState.update {
                        it.copy(
                            isPrayerTimesLoading = false,
                            prayerTimes = times,
                            locationLabel = "%.2f, %.2f".format(
                                location.latitude, location.longitude
                            ),
                        )
                    }
                }.onFailure { e ->
                    _uiState.update {
                        it.copy(
                            isPrayerTimesLoading = false,
                            prayerTimesError = e.message ?: "Failed to load prayer times",
                        )
                    }
                }
            } else {
                // Location unavailable — fall back to default city
                loadPrayerTimesByCity()
            }
        }
    }

    /**
     * Fallback: loads prayer times by default city when location is unavailable.
     */
    private suspend fun loadPrayerTimesByCity() {
        prayerTimeRepository.getPrayerTimes(
            city = "Jakarta",
            country = "Indonesia",
        ).onSuccess { times ->
            _uiState.update {
                it.copy(
                    isPrayerTimesLoading = false,
                    prayerTimes = times,
                    locationLabel = "Jakarta",
                )
            }
        }.onFailure { e ->
            _uiState.update {
                it.copy(
                    isPrayerTimesLoading = false,
                    prayerTimesError = e.message ?: "Failed to load prayer times",
                )
            }
        }
    }

    fun retryPrayerTimes() {
        loadPrayerTimesWithLocation()
    }
}
