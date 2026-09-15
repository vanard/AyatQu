package id.vanard.ayatqu.presentation.qibla

import android.app.Application
import androidx.lifecycle.viewModelScope
import id.vanard.ayatqu.R
import id.vanard.ayatqu.domain.repository.QiblaRepository
import id.vanard.ayatqu.presentation.common.viewmodel.BaseMviViewModel
import id.vanard.ayatqu.presentation.qibla.contract.QiblaEvent
import id.vanard.ayatqu.presentation.qibla.contract.QiblaSideEffect
import id.vanard.ayatqu.presentation.qibla.contract.QiblaState
import id.vanard.ayatqu.util.LocationFetchResult
import id.vanard.ayatqu.util.LocationHelper
import kotlinx.coroutines.launch

class QiblaViewModel(
    private val application: Application,
    private val repository: QiblaRepository,
    private val locationHelper: LocationHelper,
) : BaseMviViewModel<QiblaState, QiblaEvent, QiblaSideEffect>(QiblaState()) {

    override fun onEvent(event: QiblaEvent) {
        when (event) {
            is QiblaEvent.InitialData -> loadQibla(event.fetchLocation)
            is QiblaEvent.HeadingChanged -> setState {
                copy(headingDegrees = event.degrees.normalizedDegrees())
            }
            is QiblaEvent.CompassAvailabilityChanged -> setState {
                copy(isCompassAvailable = event.isAvailable)
            }
            QiblaEvent.RetryClicked -> setEffect(QiblaSideEffect.RequestLocationPermission)
            QiblaEvent.BackClicked -> setEffect(QiblaSideEffect.NavigateBack)
        }
    }

    private fun loadQibla(fetchLocation: Boolean) {
        if (uiState.value.isLoading) return
        viewModelScope.launch {
            setState { copy(isLoading = true, errorMessage = null) }
            if (!fetchLocation) {
                showError(application.getString(R.string.qibla_location_permission_required))
                return@launch
            }

            when (val result = locationHelper.getCurrentLocation()) {
                is LocationFetchResult.Success -> fetchDirection(
                    result.location.latitude,
                    result.location.longitude,
                )
                LocationFetchResult.GpsDisabled -> {
                    val lastKnown = locationHelper.getLastKnownLocation()
                    if (lastKnown != null) {
                        fetchDirection(lastKnown.latitude, lastKnown.longitude)
                    } else {
                        showError(application.getString(R.string.qibla_enable_location))
                    }
                }
                LocationFetchResult.PoorSignal -> {
                    val lastKnown = locationHelper.getLastKnownLocation()
                    if (lastKnown != null) {
                        fetchDirection(lastKnown.latitude, lastKnown.longitude)
                    } else {
                        showError(application.getString(R.string.qibla_location_unavailable))
                    }
                }
                LocationFetchResult.PermissionDenied -> showError(
                    application.getString(R.string.qibla_location_permission_required)
                )
            }
        }
    }

    private suspend fun fetchDirection(latitude: Double, longitude: Double) {
        repository.getQiblaDirection(latitude, longitude)
            .onSuccess { qibla ->
                val locationName = locationHelper.reverseGeocode(application, latitude, longitude)
                setState {
                    copy(
                        isLoading = false,
                        qibla = qibla,
                        locationName = locationName,
                        errorMessage = null,
                    )
                }
            }
            .onFailure { error ->
                showError(error.message ?: application.getString(R.string.couldnt_load_qibla))
            }
    }

    private fun showError(message: String) {
        setState { copy(isLoading = false, errorMessage = message) }
    }
}

private fun Float.normalizedDegrees(): Float = (this % 360f + 360f) % 360f
