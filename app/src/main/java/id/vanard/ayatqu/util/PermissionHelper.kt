package id.vanard.ayatqu.util

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
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

    /**
     * Checks if exact alarm permission is granted (Android 12+).
     */
    fun canScheduleExactAlarms(context: Context): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return true
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        return alarmManager.canScheduleExactAlarms()
    }

    /**
     * Opens the exact alarm permission settings page.
     */
    fun openExactAlarmSettings(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        }
    }
}
