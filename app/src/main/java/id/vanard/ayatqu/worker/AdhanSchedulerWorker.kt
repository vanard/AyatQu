package id.vanard.ayatqu.worker

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.work.*
import id.vanard.ayatqu.data.AdhanPreference
import id.vanard.ayatqu.data.AdhanScheduleCache
import id.vanard.ayatqu.data.PrayerTimeCache
import id.vanard.ayatqu.data.remote.PrayerTimeApiService
import id.vanard.ayatqu.data.repository.toPrayerTimes
import id.vanard.ayatqu.domain.model.AdhanAlarmPlan
import id.vanard.ayatqu.domain.model.AdhanDay
import id.vanard.ayatqu.util.PermissionHelper
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

class AdhanSchedulerWorker(
    appContext: Context,
    params: WorkerParameters,
    private val preferences: AdhanPreference,
    private val cache: PrayerTimeCache,
    private val scheduleCache: AdhanScheduleCache,
    private val api: PrayerTimeApiService,
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result = mutex.withLock {
        if (!preferences.notificationsEnabled.first()) {
            cancelAllAlarms(applicationContext)
            WorkManager.getInstance(applicationContext).cancelUniqueWork(WORK_NAME)
            return@withLock Result.success()
        }
        if (!PermissionHelper.isNotificationPermissionGranted(applicationContext) ||
            !PermissionHelper.canScheduleExactAlarms(applicationContext)) {
            cancelAllAlarms(applicationContext)
            return@withLock Result.success()
        }
        val zone = runCatching { ZoneId.of(cache.getCachedTimezone()) }.getOrDefault(ZoneId.systemDefault())
        val today = LocalDate.now(zone)
        val location = cache.getCachedLocation()
        val city = cache.getCachedCity()
        val locationKey = "${location ?: city}|$zone"
        val days = scheduleCache.read(locationKey).filter {
            it.date >= today.toString() && it.date <= today.plusDays(6).toString()
        }.associateBy { it.date }.toMutableMap()
        // Restore saved alarms before network work, including immediately after reboot.
        if (cache.isCacheValid()) {
            cache.cachedPrayerTimes.first()?.takeIf { it.isNotEmpty() }?.let {
                days[today.toString()] = AdhanDay(today.toString(), zone.id, it)
            }
        }
        cancelAllAlarms(applicationContext)
        arm(days.values.toList())
        var failed = false
        if (location != null || city != null) {
            for (offset in 0L..6L) {
                val date = today.plusDays(offset)
                if (days.containsKey(date.toString())) continue
                try {
                    val formattedDate = date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy", java.util.Locale.ROOT))
                    val response = if (location != null) {
                        api.getPrayerTimesByCoordinates(date = formattedDate,
                            latitude = location.first, longitude = location.second)
                    } else {
                        api.getPrayerTimesByCity(date = formattedDate, city = city!!.first, country = city.second)
                    }
                    days[date.toString()] = AdhanDay(
                        date.toString(), response.data.meta?.timezone ?: zone.id,
                        response.data.timings.toPrayerTimes(),
                    )
                    scheduleCache.save(locationKey, days.values.toList())
                    arm(listOf(days.getValue(date.toString())))
                } catch (e: CancellationException) {
                    throw e
                } catch (_: Exception) {
                    failed = true
                    break
                }
            }
        }
        scheduleCache.save(locationKey, days.values.toList())
        if (!preferences.notificationsEnabled.first()) cancelAllAlarms(applicationContext)
        if (failed && runAttemptCount < 3) Result.retry() else Result.success()
    }

    private suspend fun arm(days: List<AdhanDay>) {
        if (!preferences.notificationsEnabled.first()) return
        val manager = applicationContext.getSystemService(AlarmManager::class.java)
        for (alarm in AdhanAlarmPlan.upcoming(days, Instant.now())) {
            val intent = Intent(applicationContext, AdhanAlarmReceiver::class.java).apply {
                putExtra(EXTRA_PRAYER_NAME, alarm.prayer)
                putExtra(EXTRA_IS_ADZAN_TIME, alarm.atPrayerTime)
                putExtra(EXTRA_TRIGGER_MILLIS, alarm.triggerMillis)
            }
            try {
                manager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarm.triggerMillis,
                    PendingIntent.getBroadcast(applicationContext, alarm.requestCode, intent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE))
            } catch (_: SecurityException) {
                return // Permission was revoked while this worker was running.
            }
        }
    }

    companion object {
        private val mutex = Mutex()
        const val WORK_NAME = "adhan_scheduler"
        private const val IMMEDIATE_WORK_NAME = "adhan_scheduler_now"
        const val EXTRA_PRAYER_NAME = "prayer_name"
        const val EXTRA_IS_ADZAN_TIME = "is_adzan_time"
        const val EXTRA_TRIGGER_MILLIS = "trigger_millis"

        fun cancelAllAlarms(context: Context) {
            val manager = context.getSystemService(AlarmManager::class.java)
            // The +30000 request codes clean up snoozes created by older app versions.
            val legacyCodes = AdhanAlarmPlan.prayers.flatMap {
                listOf(it.hashCode(), it.hashCode() + 10000, it.hashCode() + 30000)
            }
            for (code in AdhanAlarmPlan.requestCodes + legacyCodes) {
                val pending = PendingIntent.getBroadcast(context, code,
                    Intent(context, AdhanAlarmReceiver::class.java),
                    PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE) ?: continue
                manager.cancel(pending)
                pending.cancel()
            }
        }

        fun enqueue(context: Context) {
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(WORK_NAME,
                ExistingPeriodicWorkPolicy.UPDATE,
                PeriodicWorkRequestBuilder<AdhanSchedulerWorker>(24, TimeUnit.HOURS)
                    .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.MINUTES).build())
        }

        fun runNow(context: Context) {
            enqueue(context)
            WorkManager.getInstance(context).enqueueUniqueWork(IMMEDIATE_WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                OneTimeWorkRequestBuilder<AdhanSchedulerWorker>()
                    .setBackoffCriteria(BackoffPolicy.EXPONENTIAL, 30, TimeUnit.MINUTES).build())
        }

        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
            WorkManager.getInstance(context).cancelUniqueWork(IMMEDIATE_WORK_NAME)
            cancelAllAlarms(context)
            context.stopService(Intent(context, id.vanard.ayatqu.service.AdhanPlaybackService::class.java))
            val notifications = context.getSystemService(android.app.NotificationManager::class.java)
            notifications.cancel(id.vanard.ayatqu.util.NotificationHelper.NOTIFICATION_ID_ADHAN)
            AdhanAlarmPlan.prayers.forEach { notifications.cancel(21001 + it.hashCode()) }
        }
    }
}
