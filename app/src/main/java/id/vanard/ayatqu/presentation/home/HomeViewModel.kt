package id.vanard.ayatqu.presentation.home

import android.app.Application
import androidx.lifecycle.viewModelScope
import id.vanard.ayatqu.data.PrayerTimeCache
import id.vanard.ayatqu.domain.model.LastRead
import id.vanard.ayatqu.domain.repository.PrayerTimeRepository
import id.vanard.ayatqu.domain.repository.QuranRepository
import id.vanard.ayatqu.domain.usecase.AuthUseCase
import id.vanard.ayatqu.presentation.common.viewmodel.BaseMviViewModel
import id.vanard.ayatqu.presentation.home.contract.HomeEvent
import id.vanard.ayatqu.presentation.home.contract.HomeSideEffect
import id.vanard.ayatqu.presentation.home.contract.HomeState
import id.vanard.ayatqu.util.LocationFetchResult
import id.vanard.ayatqu.util.LocationHelper
import id.vanard.ayatqu.util.NetworkUtils
import id.vanard.ayatqu.worker.AdhanSchedulerWorker
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// ── ViewModel ─────────────────────────────────────────────────────────────────

class HomeViewModel(
    private val quranRepository: QuranRepository,
    private val prayerTimeRepository: PrayerTimeRepository,
    private val locationHelper: LocationHelper,
    private val prayerTimeCache: PrayerTimeCache,
    private val networkUtils: NetworkUtils,
    private val application: Application,
    authUseCase: AuthUseCase,
) : BaseMviViewModel<HomeState, HomeEvent, HomeSideEffect>(
    HomeState(
        userName = authUseCase.currentUser?.displayName
            ?: authUseCase.currentUser?.email?.substringBefore("@")
            ?: "Guest",
        isNetworkAvailable = networkUtils.isNetworkAvailable(),
    )
) {

    init {
        observeLastRead()
        loadCachedData()
        observeNetwork()
    }

    override fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.LoadPrayerTimes -> loadPrayerTimesWithLocation(event.fetchLocation)
            HomeEvent.RetryClicked -> retryPrayerTimes()
            is HomeEvent.LastReadClicked -> setEffect(
                HomeSideEffect.NavigateToLastRead(event.surahNumber, event.ayahNumber)
            )
            HomeEvent.QiblaClicked -> setEffect(HomeSideEffect.NavigateToQibla)
        }
    }

    private fun observeNetwork() {
        viewModelScope.launch {
            networkUtils.networkState.collect { available ->
                setState { copy(isNetworkAvailable = available) }
            }
        }
    }

    private fun observeLastRead() {
        viewModelScope.launch {
            quranRepository.getLastRead().collect { lastRead ->
                val defaultLastRead = LastRead(
                    surahNumber = 1,
                    ayahNumber = 1,
                    surahName = "Al-Fatihah",
                )
                setState { copy(lastRead = lastRead ?: defaultLastRead) }
            }
        }
    }

    private fun loadCachedData() {
        viewModelScope.launch {
            val cachedTimes = prayerTimeCache.cachedPrayerTimes.first()
            val cachedLocation = prayerTimeCache.getCachedLocation()

            if (!cachedTimes.isNullOrEmpty()) {
                val locationName = resolveLocationName(cachedLocation)
                setState {
                    copy(
                        prayerTimes = cachedTimes,
                        locationName = locationName,
                    )
                }
            }
        }
    }

    private fun loadPrayerTimesWithLocation(fetchLocation: Boolean = true) {
        viewModelScope.launch {
            val cachedTimes = prayerTimeCache.cachedPrayerTimes.first()
            val cachedLocation = prayerTimeCache.getCachedLocation()
            val hasCache = !cachedTimes.isNullOrEmpty()

            // Show cached data immediately if available (no loading spinner)
            if (hasCache) {
                val locationName = resolveLocationName(cachedLocation)
                setState {
                    copy(
                        prayerTimes = cachedTimes,
                        locationName = locationName,
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
                setState {
                    copy(locationError = "Location permission not granted")
                }
                loadPrayerTimesByCity(hasCache = hasCache)
            }
        }
    }

    private suspend fun fetchLocationAndThenPrayerTimes(hasCache: Boolean) {
        if (!hasCache) {
            setState { copy(isLocationLoading = true, locationError = null) }
        }

        when (val result = locationHelper.getCurrentLocation()) {
            is LocationFetchResult.Success -> {
                setState { copy(isLocationLoading = false, locationError = null) }
                fetchPrayerTimesByCoordinates(
                    lat = result.location.latitude,
                    lng = result.location.longitude,
                    hasCache = hasCache,
                )
            }
            is LocationFetchResult.GpsDisabled -> {
                val lastKnown = locationHelper.getLastKnownLocation()
                if (lastKnown != null) {
                    setState { copy(isLocationLoading = false, locationError = null) }
                    fetchPrayerTimesByCoordinates(
                        lat = lastKnown.latitude,
                        lng = lastKnown.longitude,
                        hasCache = hasCache,
                    )
                } else {
                    setState {
                        copy(
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
                    setState { copy(isLocationLoading = false, locationError = null) }
                    fetchPrayerTimesByCoordinates(
                        lat = lastKnown.latitude,
                        lng = lastKnown.longitude,
                        hasCache = hasCache,
                    )
                } else {
                    setState {
                        copy(
                            isLocationLoading = false,
                            locationError = "Weak GPS signal. Using default location.",
                        )
                    }
                    loadPrayerTimesByCity(hasCache = hasCache)
                }
            }
            is LocationFetchResult.PermissionDenied -> {
                setState {
                    copy(
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
            setState { copy(isPrayerTimesLoading = true, prayerTimesError = null) }
        }

        prayerTimeRepository.getPrayerTimesByCoordinates(
            latitude = lat,
            longitude = lng,
        ).onSuccess { result ->
            val locationName = locationHelper.reverseGeocode(application, lat, lng)
            setState {
                copy(
                    isPrayerTimesLoading = false,
                    prayerTimes = result.prayerTimes,
                    locationName = locationName,
                )
            }
            AdhanSchedulerWorker.runNow(application)
        }.onFailure { e ->
            if (!hasCache) {
                setState {
                    copy(
                        isPrayerTimesLoading = false,
                        prayerTimesError = e.message ?: "Failed to load prayer times",
                    )
                }
            }
        }
    }

    private suspend fun loadPrayerTimesByCity(hasCache: Boolean) {
        if (!hasCache) {
            setState { copy(isPrayerTimesLoading = true, prayerTimesError = null) }
        }

        prayerTimeRepository.getPrayerTimes(
            city = DEFAULT_CITY,
            country = DEFAULT_COUNTRY,
        ).onSuccess { result ->
            setState {
                copy(
                    isPrayerTimesLoading = false,
                    prayerTimes = result.prayerTimes,
                    locationName = DEFAULT_CITY,
                )
            }
            AdhanSchedulerWorker.runNow(application)
        }.onFailure { e ->
            if (!hasCache) {
                setState {
                    copy(
                        isPrayerTimesLoading = false,
                        prayerTimesError = e.message ?: "Failed to load prayer times",
                    )
                }
            }
        }
    }

    private fun retryPrayerTimes(fetchLocation: Boolean = true) {
        setState {
            copy(
                prayerTimesError = null,
                locationError = null,
                isPrayerTimesLoading = false,
                isLocationLoading = false,
            )
        }
        loadPrayerTimesWithLocation(fetchLocation = fetchLocation)
    }

    private suspend fun resolveLocationName(location: Pair<Double, Double>?): String =
        location?.let { (latitude, longitude) ->
            locationHelper.reverseGeocode(application, latitude, longitude)
        } ?: DEFAULT_CITY

    private companion object {
        const val DEFAULT_CITY = "Jakarta"
        const val DEFAULT_COUNTRY = "Indonesia"
    }
}
