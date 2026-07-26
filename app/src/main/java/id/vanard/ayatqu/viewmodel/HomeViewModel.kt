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
    val timezone: String? = null,
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
            val cachedTimezone = prayerTimeCache.getCachedTimezone()

            if (!cachedTimes.isNullOrEmpty()) {
                _uiState.update {
                    it.copy(
                        prayerTimes = cachedTimes,
                        timezone = cachedTimezone?.toCityName() ?: cachedLocation?.let { loc ->
                            "%.2f, %.2f".format(loc.first, loc.second)
                        },
                    )
                }
            }
        }
    }

    fun loadPrayerTimesWithLocation(fetchLocation: Boolean = true) {
        viewModelScope.launch {
            val cachedTimes = prayerTimeCache.cachedPrayerTimes.first()
            val cachedLocation = prayerTimeCache.getCachedLocation()
            val cachedTimezone = prayerTimeCache.getCachedTimezone()
            val hasCache = !cachedTimes.isNullOrEmpty()

            // Show cached data immediately if available (no loading spinner)
            if (hasCache) {
                _uiState.update {
                    it.copy(
                        prayerTimes = cachedTimes,
                        timezone = cachedTimezone?.toCityName() ?: cachedLocation?.let { loc ->
                            "%.2f, %.2f".format(loc.first, loc.second)
                        },
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
        cachedLocation: Pair<Double, Double>?,
        fetchLocation: Boolean = true,
    ) {
        if (cachedLocation != null) {
            fetchPrayerTimesByCoordinates(
                lat = cachedLocation.first,
                lng = cachedLocation.second,
                hasCache = hasCache,
            )
        } else {
            if (fetchLocation) {
                fetchLocationAndThenPrayerTimes(hasCache = hasCache)
            } else {
                _uiState.update {
                    it.copy(locationError = "Location permission not granted")
                }
                loadPrayerTimesByCity(hasCache = hasCache)
            }
        }
    }

    private suspend fun fetchLocationAndThenPrayerTimes(hasCache: Boolean) {
        if (!hasCache) {
            _uiState.update { it.copy(isLocationLoading = true, locationError = null) }
        }

        when (val result = locationHelper.getCurrentLocation()) {
            is LocationFetchResult.Success -> {
                _uiState.update { it.copy(isLocationLoading = false, locationError = null) }
                fetchPrayerTimesByCoordinates(
                    lat = result.location.latitude,
                    lng = result.location.longitude,
                    hasCache = hasCache,
                )
            }
            is LocationFetchResult.GpsDisabled -> {
                val lastKnown = locationHelper.getLastKnownLocation()
                if (lastKnown != null) {
                    _uiState.update { it.copy(isLocationLoading = false, locationError = null) }
                    fetchPrayerTimesByCoordinates(
                        lat = lastKnown.latitude,
                        lng = lastKnown.longitude,
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
                    fetchPrayerTimesByCoordinates(
                        lat = lastKnown.latitude,
                        lng = lastKnown.longitude,
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
        hasCache: Boolean,
    ) {
        if (!hasCache) {
            _uiState.update { it.copy(isPrayerTimesLoading = true, prayerTimesError = null) }
        }

        prayerTimeRepository.getPrayerTimesByCoordinates(
            latitude = lat,
            longitude = lng,
        ).onSuccess { result ->
            _uiState.update {
                it.copy(
                    isPrayerTimesLoading = false,
                    prayerTimes = result.prayerTimes,
                    timezone = result.timezone?.toCityName() ?: "%.2f, %.2f".format(lat, lng),
                )
            }
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
        ).onSuccess { result ->
            _uiState.update {
                it.copy(
                    isPrayerTimesLoading = false,
                    prayerTimes = result.prayerTimes,
                    timezone = result.timezone?.toCityName(),
                )
            }
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

    private fun String.toCityName(): String =substringAfterLast("/").replace("_", " ")
}
