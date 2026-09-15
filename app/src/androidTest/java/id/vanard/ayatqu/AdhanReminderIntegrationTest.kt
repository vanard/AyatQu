package id.vanard.ayatqu

import android.Manifest
import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.ParcelFileDescriptor
import androidx.test.platform.app.InstrumentationRegistry
import androidx.work.WorkInfo
import androidx.work.WorkManager
import id.vanard.ayatqu.data.*
import id.vanard.ayatqu.domain.model.*
import id.vanard.ayatqu.util.NotificationHelper
import id.vanard.ayatqu.worker.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate
import java.time.ZoneId

class AdhanReminderIntegrationTest {
    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val context = instrumentation.targetContext

    private fun shell(command: String): String = ParcelFileDescriptor.AutoCloseInputStream(
        instrumentation.uiAutomation.executeShellCommand(command)).bufferedReader().use { it.readText() }

    private suspend fun awaitScheduling(previousId: java.util.UUID?) {
        withTimeout(20000) {
            while (true) {
                val work = WorkManager.getInstance(context).getWorkInfosForUniqueWork("adhan_scheduler_now").get().firstOrNull()
                if (work != null && work.id != previousId && work.state.isFinished) {
                    assertEquals(WorkInfo.State.SUCCEEDED, work.state)
                    break
                }
                delay(100)
            }
        }
    }

    @Test fun restoresSavedAlarmsPlaysSelectedAdhanAndCancelsWhenDisabled() = runBlocking {
        if (Build.VERSION.SDK_INT >= 33) instrumentation.uiAutomation.grantRuntimePermission(context.packageName, Manifest.permission.POST_NOTIFICATIONS)
        if (Build.VERSION.SDK_INT >= 31) shell("appops set ${context.packageName} SCHEDULE_EXACT_ALARM allow")
        val snapshot = context.dataStore.data.first()
        val prefs = AdhanPreference(context)
        val manager = context.getSystemService(AlarmManager::class.java)
            val notifications = context.getSystemService(NotificationManager::class.java)
        val workManager = WorkManager.getInstance(context)
        var testAlarm: PendingIntent? = null
        try {
            NotificationHelper.createNotificationChannels(context)
            val reminderChannel = notifications.getNotificationChannel(NotificationHelper.CHANNEL_PRE_ADHAN)
            assertNull(reminderChannel.sound)
            assertFalse(reminderChannel.shouldVibrate())
            prefs.setNotificationsEnabled(true)
            prefs.setAdhanSoundType(AdhanPreference.SOUND_TYPE_DEFAULT)
            val zone = ZoneId.systemDefault()
            val today = LocalDate.now(zone)
            val location = -6.2 to 106.8
            val prayers = AdhanAlarmPlan.prayers.map { PrayerTime(it, "23:55") }
            PrayerTimeCache(context).savePrayerTimes(prayers, location.first, location.second, zone.id)
            AdhanScheduleCache(context).save("$location|$zone", (0L..6L).map {
                AdhanDay(today.plusDays(it).toString(), zone.id, prayers)
            })
            val previousId = workManager.getWorkInfosForUniqueWork("adhan_scheduler_now").get().firstOrNull()?.id
            withContext(Dispatchers.Main) {
                BootCompletedReceiver().onReceive(context, Intent(Intent.ACTION_BOOT_COMPLETED))
            }
            awaitScheduling(previousId)
            val tomorrow = AdhanAlarmPlan.upcoming(listOf(AdhanDay(today.plusDays(1).toString(), zone.id, prayers)), java.time.Instant.now())
            tomorrow.forEach { alarm ->
                assertNotNull(PendingIntent.getBroadcast(context, alarm.requestCode,
                    Intent(context, AdhanAlarmReceiver::class.java), PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE))
            }
            assertTrue(shell("dumpsys alarm").contains("${context.packageName}/.worker.AdhanAlarmReceiver"))

            // Exercise actual AlarmManager delivery, receiver, foreground audio, and notification Stop.
            val trigger = System.currentTimeMillis() + 1500
            testAlarm = PendingIntent.getBroadcast(context, 49999,
                Intent(context, AdhanAlarmReceiver::class.java)
                    .putExtra(AdhanSchedulerWorker.EXTRA_PRAYER_NAME, "Fajr")
                    .putExtra(AdhanSchedulerWorker.EXTRA_IS_ADZAN_TIME, true)
                    .putExtra(AdhanSchedulerWorker.EXTRA_TRIGGER_MILLIS, trigger),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, trigger, testAlarm)
            val playing = withTimeout(15000) {
                while (true) {
                    val n = notifications.activeNotifications.firstOrNull {
                        it.id == NotificationHelper.NOTIFICATION_ID_ADHAN &&
                            it.notification.flags and Notification.FLAG_ONGOING_EVENT != 0
                    }
                    if (n != null) return@withTimeout n.notification
                    delay(100)
                }
                @Suppress("UNREACHABLE_CODE") error("unreachable")
            }
            playing.actions.first().actionIntent.send()
            withTimeout(5000) {
                while (notifications.activeNotifications.any {
                    it.id == NotificationHelper.NOTIFICATION_ID_ADHAN && it.notification.flags and Notification.FLAG_ONGOING_EVENT != 0
                }) delay(100)
            }
            prefs.setNotificationsEnabled(false)
            AdhanSchedulerWorker.cancel(context)
            tomorrow.forEach { alarm ->
                assertNull(PendingIntent.getBroadcast(context, alarm.requestCode,
                    Intent(context, AdhanAlarmReceiver::class.java), PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE))
            }
        } finally {
            testAlarm?.let { manager.cancel(it); it.cancel() }
            AdhanSchedulerWorker.cancel(context)
            notifications.cancel(NotificationHelper.NOTIFICATION_ID_ADHAN)
            context.dataStore.updateData { snapshot }
            AdhanSchedulerWorker.runNow(context)
        }
    }
}
