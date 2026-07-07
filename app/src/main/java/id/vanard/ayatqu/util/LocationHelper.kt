package id.vanard.ayatqu.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.os.Looper
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Helper to fetch the user's current location once.
 *
 * Requires ACCESS_FINE_LOCATION or ACCESS_COARSE_LOCATION permission
 * to be granted before calling [getCurrentLocation].
 */
class LocationHelper(context: Context) {

    private val fusedClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

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

    /**
     * Fetches the current location once.
     * Returns null if permission is not granted or location cannot be determined.
     */
    suspend fun getCurrentLocation(): Location? {
        return suspendCancellableCoroutine { continuation ->
            val locationRequest = LocationRequest.Builder(
                Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                1000L,
            ).setMaxUpdates(1).build()

            val callback = object : LocationCallback() {
                override fun onLocationResult(result: LocationResult) {
                    fusedClient.removeLocationUpdates(this)
                    val location = result.lastLocation
                    if (continuation.isActive) {
                        continuation.resume(location)
                    }
                }
            }

            try {
                fusedClient.requestLocationUpdates(
                    locationRequest,
                    callback,
                    Looper.getMainLooper(),
                )
            } catch (e: SecurityException) {
                if (continuation.isActive) {
                    continuation.resume(null)
                }
            }

            continuation.invokeOnCancellation {
                fusedClient.removeLocationUpdates(callback)
            }
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
