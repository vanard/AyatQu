package id.vanard.ayatqu.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat

object PermissionHelper {

    const val NOTIFICATION_PERMISSION_REQUEST_CODE = 1001

    /**
     * Checks if the notification permission is required and granted.
     * On Android 13+ (API 33+), the POST_NOTIFICATIONS permission is required.
     * On older versions, notifications are allowed by default.
     */
    fun isNotificationPermissionRequired(context: Context): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
    }

    /**
     * Checks if the notification permission is already granted.
     * Returns true if not required (Android 12 and below) or if granted.
     */
    fun isNotificationPermissionGranted(context: Context): Boolean {
        if (!isNotificationPermissionRequired(context)) return true
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Returns the permission string for Android 13+.
     */
    fun getNotificationPermission(): String = Manifest.permission.POST_NOTIFICATIONS
}
