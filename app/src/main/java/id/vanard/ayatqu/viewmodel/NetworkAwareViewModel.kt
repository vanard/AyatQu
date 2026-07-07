package id.vanard.ayatqu.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import id.vanard.ayatqu.util.NetworkUtils
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

/**
 * Base ViewModel that automatically tracks network connectivity.
 *
 * Subclasses get [isNetworkAvailable] as a [StateFlow] that emits
 * `true` when the device has internet and `false` otherwise.
 *
 * Screens that need API calls should extend this class and observe
 * [isNetworkAvailable] to show a "no connection" UI when offline.
 */
abstract class NetworkAwareViewModel(
    private val networkUtils: NetworkUtils,
) : ViewModel() {

    /**
     * Current network connectivity state.
     * - `true` = device has internet
     * - `false` = no internet connection
     */
    val isNetworkAvailable: StateFlow<Boolean> = networkUtils.networkState
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = networkUtils.isNetworkAvailable(),
        )
}
