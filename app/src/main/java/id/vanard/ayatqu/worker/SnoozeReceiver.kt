package id.vanard.ayatqu.worker

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import id.vanard.ayatqu.util.NotificationHelper

class SnoozeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val prayerName = intent.getStringExtra(NotificationHelper.EXTRA_PRAYER_NAME) ?: return
        val soundType = intent.getStringExtra(NotificationHelper.EXTRA_SOUND_TYPE) ?: return

        val manager = context.getSystemService(NotificationManager::class.java)
        manager.cancel(NotificationHelper.NOTIFICATION_ID_ADHAN + prayerName.hashCode())

        NotificationHelper.scheduleSnoozeAlarm(
            context = context,
            prayerName = prayerName,
            soundType = soundType,
            snoozeMinutes = 5,
        )
    }
}
