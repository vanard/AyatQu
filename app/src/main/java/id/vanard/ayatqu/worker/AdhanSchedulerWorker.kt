package id.vanard.ayatqu.worker

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import id.vanard.ayatqu.data.AdhanPreference
import id.vanard.ayatqu.data.PrayerTimeCache
import kotlinx.coroutines.flow.first
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit

class AdhanSchedulerWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val preferences = AdhanPreference(applicationContext)
        val cache = PrayerTimeCache(applicationContext)

        val notificationsEnabled = preferences.notificationsEnabled.first()
        if (!notificationsEnabled) {
            cancelAllAlarms(applicationContext)
            return Result.success()
        }

        val prayerTimes = cache.cachedPrayerTimes.first()
        if (prayerTimes.isNullOrEmpty()) return Result.success()

        val soundType = preferences.adhanSoundType.first()
        val now = LocalDateTime.now()
        val today = LocalDate.now()

        for (prayer in prayerTimes) {
            val prayerName = prayer.name
            if (prayerName == "Sunrise") continue

            val timeParts = prayer.time.split(":")
            if (timeParts.size != 2) continue
            val hour = timeParts[0].toIntOrNull() ?: continue
            val minute = timeParts[1].toIntOrNull() ?: continue

            val prayerDateTime = LocalDateTime.of(today, LocalTime.of(hour, minute))

            // Schedule 5-minute-before alarm
            val preAdhanTime = prayerDateTime.minusMinutes(5)
            if (preAdhanTime.isAfter(now)) {
                scheduleAlarm(
                    context = applicationContext,
                    triggerAt = preAdhanTime,
                    prayerName = prayerName,
                    isAdzanTime = false,
                    requestCode = getRequestCode(prayerName, isAdzanTime = false),
                )
            }

            // Schedule adzan-time alarm
            if (prayerDateTime.isAfter(now)) {
                scheduleAlarm(
                    context = applicationContext,
                    triggerAt = prayerDateTime,
                    prayerName = prayerName,
                    isAdzanTime = true,
                    requestCode = getRequestCode(prayerName, isAdzanTime = true),
                )
            }
        }

        return Result.success()
    }

    private fun scheduleAlarm(
        context: Context,
        triggerAt: LocalDateTime,
        prayerName: String,
        isAdzanTime: Boolean,
        requestCode: Int,
    ) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)

        val intent = Intent(context, AdhanAlarmReceiver::class.java).apply {
            putExtra(EXTRA_PRAYER_NAME, prayerName)
            putExtra(EXTRA_IS_ADZAN_TIME, isAdzanTime)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val triggerMillis = triggerAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerMillis,
                    pendingIntent,
                )
            } else {
                // Fallback to inexact alarm if exact alarm permission not granted
                alarmManager.set(AlarmManager.RTC_WAKEUP, triggerMillis, pendingIntent)
            }
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerMillis,
                pendingIntent,
            )
        }
    }

    private fun cancelAllAlarms(context: Context) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        val prayers = listOf("Fajr", "Dhuhr", "Asr", "Maghrib", "Isha")

        for (prayer in prayers) {
            for (isAdzan in listOf(true, false)) {
                val intent = Intent(context, AdhanAlarmReceiver::class.java)
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    getRequestCode(prayer, isAdzan),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
                )
                alarmManager.cancel(pendingIntent)
            }
        }
    }

    companion object {
        const val WORK_NAME = "adhan_scheduler"
        const val EXTRA_PRAYER_NAME = "prayer_name"
        const val EXTRA_IS_ADZAN_TIME = "is_adzan_time"

        private fun getRequestCode(prayerName: String, isAdzanTime: Boolean): Int {
            val base = prayerName.hashCode()
            return if (isAdzanTime) base else base + 10000
        }

        fun enqueue(context: Context) {
            val request = PeriodicWorkRequestBuilder<AdhanSchedulerWorker>(
                repeatInterval = 24,
                repeatIntervalTimeUnit = TimeUnit.HOURS,
            ).build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request,
            )
        }

        fun runNow(context: Context) {
            val request = PeriodicWorkRequestBuilder<AdhanSchedulerWorker>(
                repeatInterval = 24,
                repeatIntervalTimeUnit = TimeUnit.HOURS,
            ).build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                request,
            )
        }
    }
}
