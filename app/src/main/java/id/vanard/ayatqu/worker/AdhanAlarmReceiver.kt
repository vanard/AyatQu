package id.vanard.ayatqu.worker

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import id.vanard.ayatqu.data.AdhanPreference
import id.vanard.ayatqu.service.AdhanPlaybackService
import id.vanard.ayatqu.util.NotificationHelper
import id.vanard.ayatqu.util.PermissionHelper
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first

class AdhanAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val prayer = intent.getStringExtra(AdhanSchedulerWorker.EXTRA_PRAYER_NAME) ?: return
        val trigger = intent.getLongExtra(AdhanSchedulerWorker.EXTRA_TRIGGER_MILLIS, 0)
        // Do not play missed prayers in a burst after a prolonged shutdown or restriction.
        if (trigger == 0L || System.currentTimeMillis() - trigger !in 0..5 * 60 * 1000L) return
        val pending = goAsync()
        CoroutineScope(Dispatchers.Main.immediate).launch {
            try {
                withTimeout(8000) {
                    val prefs = AdhanPreference(context)
                    if (!prefs.notificationsEnabled.first() || !PermissionHelper.isNotificationPermissionGranted(context)) return@withTimeout
                    if (!intent.getBooleanExtra(AdhanSchedulerWorker.EXTRA_IS_ADZAN_TIME, false)) {
                        NotificationHelper.showPreAdhanNotification(context, prayer)
                        return@withTimeout
                    }
                    val manager = context.getSystemService(NotificationManager::class.java)
                    val silent = prefs.adhanSoundType.first() == AdhanPreference.SOUND_TYPE_SILENT ||
                        manager.currentInterruptionFilter != NotificationManager.INTERRUPTION_FILTER_ALL ||
                        manager.getNotificationChannel(NotificationHelper.CHANNEL_ADHAN)?.importance == NotificationManager.IMPORTANCE_NONE
                    NotificationHelper.showAdhanNotification(context, prayer)
                    if (!silent) {
                        try {
                            context.startForegroundService(Intent(context, AdhanPlaybackService::class.java)
                                .putExtra(NotificationHelper.EXTRA_PRAYER_NAME, prayer))
                        } catch (_: IllegalStateException) {
                            // Keep the notification if the system disallows background playback.
                        } catch (_: SecurityException) { }
                    }
                }
            } finally { pending.finish() }
        }
    }
}
