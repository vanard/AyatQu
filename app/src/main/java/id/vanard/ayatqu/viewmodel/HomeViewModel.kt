package id.vanard.ayatqu.viewmodel

import android.app.Application
import androidx.lifecycle.viewModelScope
import id.vanard.ayatqu.data.PrayerTimeCache
import id.vanard.ayatqu.domain.model.LastRead
import id.vanard.ayatqu.domain.model.PrayerTime
import id.vanard.ayatqu.domain.repository.PrayerTimeRepository
import id.vanard.ayatqu.domain.repository.QuranRepository
import id.vanard.ayatqu.util.LocationFetchResult
import id.vanard.ayatqu.util.LocationHelper
import id.vanard.ayatqu.util.NetworkUtils
import id.vanard.ayatqu.worker.AdhanSchedulerWorker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// ── UI State ──────────────────────────────────────────────────────────────────

data class HomeUiState(
    val lastRead: LastRead? = null,
    val prayerTimes: List<PrayerTime> = emptyList(),
    val isPrayerTimesLoading: Boolean = false,
    val prayerTimesError: String? = null,
    val locationLabel: String = "Your Location",
    val isLocationLoading: Boolean = false,
    val locationError: String? = null,
)

// ── ViewModel ─────────────────────────────────────────────────────────────────

class HomeViewModel(
    private val quranRepository: QuranRepository,
    private val prayerTimeRepository: PrayerTimeRepository,
    private val locationHelper: LocationHelper,
    private val prayerTimeCache: PrayerTimeCache,
    networkUtils: NetworkUtils,
    private val application: Application,
) : NetworkAwareViewModel(networkUtils) {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeLastRead()
        loadCachedData()
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

    private fun loadCachedData() {
        viewModelScope.launch {
            val cachedTimes = prayerTimeCache.cachedPrayerTimes.first()
            val cachedLocation = prayerTimeCache.getCachedLocation()

            if (!cachedTimes.isNullOrEmpty()) {
                _uiState.update {
                    it.copy(
                        prayerTimes = cachedTimes,
                        locationLabel = cachedLocation?.third ?: it.locationLabel,
                    )
                }
            }
        }
    }

    fun loadPrayerTimesWithLocation(fetchLocation: Boolean = true) {
        viewModelScope.launch {
            val cachedTimes = prayerTimeCache.cachedPrayerTimes.first()
            val cachedLocation = prayerTimeCache.getCachedLocation()
            val hasCache = !cachedTimes.isNullOrEmpty()

            // Show cached data immediately if available (no loading spinner)
            if (hasCache) {
                _uiState.update {
                    it.copy(
                        prayerTimes = cachedTimes,
                        locationLabel = cachedLocation?.third ?: it.locationLabel,
                        locationError = null,
                        prayerTimesError = null,
                    )
                }
            }

            // Fetch fresh data in background
            fetchFreshPrayerTimes(
                hasCache = hasCache,
                cachedLocation = cachedLocation,
                fetchLocation = fetchLocation,
            )
        }
    }

    private suspend fun fetchFreshPrayerTimes(
        hasCache: Boolean,
        cachedLocation: Triple<Double?, Double?, String?>?,
        fetchLocation: Boolean = true,
    ) {
        if (cachedLocation != null) {
            // Use cached coordinates — no location loading needed
            val lat = cachedLocation.first!!
            val lng = cachedLocation.second!!
            fetchPrayerTimesByCoordinates(
                lat = lat,
                lng = lng,
                label = cachedLocation.third,
                hasCache = hasCache,
            )
        } else {
            // No cached coordinates — need to get location
            if (fetchLocation) {
                fetchLocationAndThenPrayerTimes(hasCache = hasCache)
            } else {
                // Permission not granted — skip location, use city fallback
                _uiState.update {
                    it.copy(
                        locationError = "Location permission not granted",
                    )
                }
                loadPrayerTimesByCity(hasCache = hasCache)
            }
        }
    }

    private suspend fun fetchLocationAndThenPrayerTimes(hasCache: Boolean) {
        // Show location loading only if we don't have prayer times to show yet
        if (!hasCache) {
            _uiState.update { it.copy(isLocationLoading = true, locationError = null) }
        }

        val result = locationHelper.getCurrentLocation()

        when (result) {
            is LocationFetchResult.Success -> {
                _uiState.update { it.copy(isLocationLoading = false, locationError = null) }
                val label = locationHelper.reverseGeocode(
                    application, result.location.latitude, result.location.longitude,
                )
                fetchPrayerTimesByCoordinates(
                    lat = result.location.latitude,
                    lng = result.location.longitude,
                    label = label,
                    hasCache = hasCache,
                )
            }
            is LocationFetchResult.GpsDisabled -> {
                val lastKnown = locationHelper.getLastKnownLocation()
                if (lastKnown != null) {
                    _uiState.update { it.copy(isLocationLoading = false, locationError = null) }
                    val label = locationHelper.reverseGeocode(
                        application, lastKnown.latitude, lastKnown.longitude,
                    )
                    fetchPrayerTimesByCoordinates(
                        lat = lastKnown.latitude,
                        lng = lastKnown.longitude,
                        label = label,
                        hasCache = hasCache,
                    )
                } else {
                    _uiState.update {
                        it.copy(
                            isLocationLoading = false,
                            locationError = "GPS is disabled. Please enable location services.",
                        )
                    }
                    loadPrayerTimesByCity(hasCache = hasCache)
                }
            }
            is LocationFetchResult.PoorSignal -> {
                val lastKnown = locationHelper.getLastKnownLocation()
                if (lastKnown != null) {
                    _uiState.update { it.copy(isLocationLoading = false, locationError = null) }
                    val label = locationHelper.reverseGeocode(
                        application, lastKnown.latitude, lastKnown.longitude,
                    )
                    fetchPrayerTimesByCoordinates(
                        lat = lastKnown.latitude,
                        lng = lastKnown.longitude,
                        label = label,
                        hasCache = hasCache,
                    )
                } else {
                    _uiState.update {
                        it.copy(
                            isLocationLoading = false,
                            locationError = "Weak GPS signal. Using default location.",
                        )
                    }
                    loadPrayerTimesByCity(hasCache = hasCache)
                }
            }
            is LocationFetchResult.PermissionDenied -> {
                _uiState.update {
                    it.copy(
                        isLocationLoading = false,
                        locationError = "Location permission denied.",
                    )
                }
                loadPrayerTimesByCity(hasCache = hasCache)
            }
        }
    }

    private suspend fun fetchPrayerTimesByCoordinates(
        lat: Double,
        lng: Double,
        label: String?,
        hasCache: Boolean,
    ) {
        if (!hasCache) {
            _uiState.update { it.copy(isPrayerTimesLoading = true, prayerTimesError = null) }
        }

        prayerTimeRepository.getPrayerTimesByCoordinates(
            latitude = lat,
            longitude = lng,
            locationLabel = label,
        ).onSuccess { times ->
            _uiState.update {
                it.copy(
                    isPrayerTimesLoading = false,
                    prayerTimes = times,
                    locationLabel = label ?: it.locationLabel,
                )
            }
            // Re-schedule adhan alarms with fresh prayer times
            AdhanSchedulerWorker.runNow(application)
        }.onFailure { e ->
            if (!hasCache) {
                _uiState.update {
                    it.copy(
                        isPrayerTimesLoading = false,
                        prayerTimesError = e.message ?: "Failed to load prayer times",
                    )
                }
            }
        }
    }

    private suspend fun loadPrayerTimesByCity(hasCache: Boolean) {
        if (!hasCache) {
            _uiState.update { it.copy(isPrayerTimesLoading = true, prayerTimesError = null) }
        }

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
            // Re-schedule adhan alarms with fresh prayer times
            AdhanSchedulerWorker.runNow(application)
        }.onFailure { e ->
            if (!hasCache) {
                _uiState.update {
                    it.copy(
                        isPrayerTimesLoading = false,
                        prayerTimesError = e.message ?: "Failed to load prayer times",
                    )
                }
            }
        }
    }

    fun retryPrayerTimes(fetchLocation: Boolean = true) {
        _uiState.update {
            it.copy(
                prayerTimesError = null,
                locationError = null,
                isPrayerTimesLoading = false,
                isLocationLoading = false,
            )
        }
        loadPrayerTimesWithLocation(fetchLocation = fetchLocation)
    }
}
