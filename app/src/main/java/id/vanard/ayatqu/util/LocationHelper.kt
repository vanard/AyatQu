package id.vanard.ayatqu.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.resume
import kotlin.time.Duration.Companion.milliseconds

sealed class LocationFetchResult {
    data class Success(val location: Location) : LocationFetchResult()
    data object GpsDisabled : LocationFetchResult()
    data object PoorSignal : LocationFetchResult()
    data object PermissionDenied : LocationFetchResult()
}

class LocationHelper(context: Context) {

    private val fusedClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val locationManager =
        context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    companion object {
        const val LOCATION_PERMISSION_REQUEST_CODE = 2001

        fun isLocationPermissionGranted(context: Context): Boolean {
            return ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION,
            ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                ) == PackageManager.PERMISSION_GRANTED
        }

        fun getLocationPermissions(): Array<String> = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
        )
    }

    fun isLocationEnabled(): Boolean {
        val gpsEnabled = try {
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (_: SecurityException) {
            false
        }
        val networkEnabled = try {
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (_: SecurityException) {
            false
        }
        return gpsEnabled || networkEnabled
    }

    /**
     * Fetches the current location once.
     * Returns a typed result indicating success, GPS disabled, poor signal, or permission denied.
     * Times out after 1 second to prevent indefinite hanging.
     */
    suspend fun getCurrentLocation(): LocationFetchResult {
        if (!isLocationEnabled()) {
            return LocationFetchResult.GpsDisabled
        }

        val location = withTimeoutOrNull(1_000L.milliseconds) {
            suspendCancellableCoroutine { continuation ->
                val locationRequest = LocationRequest.Builder(
                    Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                    100L,
                ).setMaxUpdates(1).build()

                val callback = object : LocationCallback() {
                    override fun onLocationResult(result: LocationResult) {
                        fusedClient.removeLocationUpdates(this)
                        val loc = result.lastLocation
                        if (continuation.isActive) {
                            continuation.resume(loc)
                        }
                    }
                }

                try {
                    fusedClient.requestLocationUpdates(
                        locationRequest,
                        callback,
                        Looper.getMainLooper(),
                    )
                } catch (_: SecurityException) {
                    if (continuation.isActive) {
                        continuation.resume(null)
                    }
                }

                continuation.invokeOnCancellation {
                    fusedClient.removeLocationUpdates(callback)
                }
            }
        }

        return when {
            location != null -> LocationFetchResult.Success(location)
            else -> LocationFetchResult.PoorSignal
        }
    }

    /**
     * Returns the last known location (faster but may be stale).
     * Falls back to null if unavailable.
     */
    suspend fun getLastKnownLocation(): Location? {
        return suspendCancellableCoroutine { continuation ->
            try {
                fusedClient.lastLocation
                    .addOnSuccessListener { location ->
                        if (continuation.isActive) {
                            continuation.resume(location)
                        }
                    }
                    .addOnFailureListener {
                        if (continuation.isActive) {
                            continuation.resume(null)
                        }
                    }
            } catch (e: SecurityException) {
                if (continuation.isActive) {
                    continuation.resume(null)
                }
            }
        }
    }
}
