package id.vanard.ayatqu.util

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import id.vanard.ayatqu.MainActivity
import id.vanard.ayatqu.R
import id.vanard.ayatqu.data.AdhanPreference
import id.vanard.ayatqu.worker.SnoozeReceiver
import java.time.LocalDateTime
import java.time.ZoneId

object NotificationHelper {

    const val CHANNEL_PRE_ADHAN = "adhan_pre_channel"
    const val CHANNEL_ADHAN = "adhan_channel"

    const val NOTIFICATION_ID_PRE_ADHAN = 2001
    const val NOTIFICATION_ID_ADHAN = 2002

    const val EXTRA_PRAYER_NAME = "prayer_name"
    const val EXTRA_SOUND_TYPE = "sound_type"

    fun createNotificationChannels(context: Context) {
        val manager = context.getSystemService(NotificationManager::class.java)

        val preAdhanChannel = NotificationChannel(
            CHANNEL_PRE_ADHAN,
            "Adhan Reminder",
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply {
            description = "Heads-up reminder 5 minutes before prayer time"
            enableVibration(true)
        }

        val adhanChannel = NotificationChannel(
            CHANNEL_ADHAN,
            "Adhan",
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            description = "Notification when it's time for prayer"
            enableVibration(true)
        }

        manager.createNotificationChannels(listOf(preAdhanChannel, adhanChannel))
    }

    fun showPreAdhanNotification(context: Context, prayerName: String) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            prayerName.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_PRE_ADHAN)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Prayer Reminder")
            .setContentText("Prayer time in 5 minutes: $prayerName")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val manager = context.getSystemService(NotificationManager::class.java)
        manager.notify(NOTIFICATION_ID_PRE_ADHAN + prayerName.hashCode(), notification)
    }

    fun showAdhanNotification(
        context: Context,
        prayerName: String,
        soundType: String,
    ) {
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            prayerName.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val snoozeIntent = Intent(context, SnoozeReceiver::class.java).apply {
            putExtra(EXTRA_PRAYER_NAME, prayerName)
            putExtra(EXTRA_SOUND_TYPE, soundType)
        }
        val snoozePendingIntent = PendingIntent.getBroadcast(
            context,
            prayerName.hashCode() + 20000,
            snoozeIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val builder = NotificationCompat.Builder(context, CHANNEL_ADHAN)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Adhan")
            .setContentText("It's time for $prayerName prayer")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .addAction(R.drawable.ic_launcher_foreground, "Snooze 5 min", snoozePendingIntent)

        when (soundType) {
            AdhanPreference.SOUND_TYPE_SILENT -> {
                builder.setSilent(true)
            }
            else -> {
                val uri = android.net.Uri.parse(
                    "android.resource://${context.packageName}/${AdhanPreference.getRawResId(soundType)}"
                )
                builder.setSound(uri)
            }
        }

        val manager = context.getSystemService(NotificationManager::class.java)
        manager.notify(NOTIFICATION_ID_ADHAN + prayerName.hashCode(), builder.build())
    }

    fun scheduleSnoozeAlarm(
        context: Context,
        prayerName: String,
        soundType: String,
        snoozeMinutes: Long = 5,
    ) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        val triggerAt = LocalDateTime.now().plusMinutes(snoozeMinutes)
        val triggerMillis = triggerAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        val intent = Intent(context, id.vanard.ayatqu.worker.AdhanAlarmReceiver::class.java).apply {
            putExtra(id.vanard.ayatqu.worker.AdhanSchedulerWorker.EXTRA_PRAYER_NAME, prayerName)
            putExtra(id.vanard.ayatqu.worker.AdhanSchedulerWorker.EXTRA_IS_ADZAN_TIME, true)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            prayerName.hashCode() + 30000,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerMillis,
            pendingIntent,
        )
    }
}
