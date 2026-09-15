package id.vanard.ayatqu.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import id.vanard.ayatqu.MainActivity
import id.vanard.ayatqu.R
import id.vanard.ayatqu.service.AdhanPlaybackService

object NotificationHelper {
    // Channel settings cannot be changed after creation, so v2 replaces the old
    // audible reminder channel for users upgrading the app.
    const val CHANNEL_PRE_ADHAN = "adhan_pre_silent_v2"
    // A new silent channel prevents the old channel's sound playing over the chosen adzan.
    const val CHANNEL_ADHAN = "adhan_playback_v2"
    const val NOTIFICATION_ID_ADHAN = 22002
    const val EXTRA_PRAYER_NAME = "prayer_name"
    const val EXTRA_SOUND_TYPE = "sound_type"

    fun createNotificationChannels(context: Context) {
        context.getSystemService(NotificationManager::class.java).createNotificationChannels(listOf(
            NotificationChannel(CHANNEL_PRE_ADHAN, context.getString(R.string.prayer_reminder),
                NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = context.getString(R.string.reminder_channel_description)
                setSound(null, null)
                enableVibration(false)
            },
            NotificationChannel(CHANNEL_ADHAN, context.getString(R.string.adhan_sound),
                NotificationManager.IMPORTANCE_HIGH).apply {
                setSound(null, null)
                enableVibration(false)
            },
        ))
    }

    private fun openApp(context: Context) = PendingIntent.getActivity(context, 2002,
        Intent(context, MainActivity::class.java), PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)

    fun showPreAdhanNotification(context: Context, prayerName: String) {
        if (!PermissionHelper.isNotificationPermissionGranted(context)) return
        val notification = NotificationCompat.Builder(context, CHANNEL_PRE_ADHAN)
            .setSmallIcon(R.drawable.home_05_stroke_rounded)
            .setContentTitle(context.getString(R.string.prayer_reminder))
            .setContentText(context.getString(R.string.prayer_in_15_minutes, prayerName))
            .setContentIntent(openApp(context)).setSilent(true).setAutoCancel(true).build()
        context.getSystemService(NotificationManager::class.java).notify(21001 + prayerName.hashCode(), notification)
    }

    fun adhanNotification(context: Context, prayerName: String, playing: Boolean): Notification {
        val builder = NotificationCompat.Builder(context, CHANNEL_ADHAN)
            .setSmallIcon(R.drawable.home_05_stroke_rounded)
            .setContentTitle(context.getString(R.string.adhan_sound))
            .setContentText(context.getString(R.string.prayer_time_now, prayerName))
            .setContentIntent(openApp(context)).setSilent(true)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setOngoing(playing).setAutoCancel(!playing)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
        if (playing) {
            val stop = PendingIntent.getService(context, 2003,
                Intent(context, AdhanPlaybackService::class.java).setAction(AdhanPlaybackService.ACTION_STOP),
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
            builder.addAction(0, context.getString(R.string.stop_adhan), stop)
        }
        return builder.build()
    }

    fun showAdhanNotification(context: Context, prayerName: String) {
        if (!PermissionHelper.isNotificationPermissionGranted(context)) return
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.cancel(21001 + prayerName.hashCode())
        manager.notify(NOTIFICATION_ID_ADHAN, adhanNotification(context, prayerName, false))
    }
}
