package id.vanard.ayatqu.domain.model

import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

data class AdhanDay(val date: String, val timezone: String, val prayers: List<PrayerTime>)

data class AdhanAlarm(val requestCode: Int, val prayer: String, val atPrayerTime: Boolean, val triggerMillis: Long)

object AdhanAlarmPlan {
    val prayers = listOf("Fajr", "Dhuhr", "Asr", "Maghrib", "Isha")
    // Eight reusable day slots accommodate the seven-day schedule without collisions.
    val requestCodes = 40000 until 40080

    fun upcoming(days: List<AdhanDay>, now: Instant): List<AdhanAlarm> = days.flatMap { day ->
        val date = runCatching { LocalDate.parse(day.date) }.getOrNull() ?: return@flatMap emptyList()
        val zone = runCatching { ZoneId.of(day.timezone) }.getOrNull() ?: return@flatMap emptyList()
        day.prayers.flatMap prayers@{ prayer ->
            val index = prayers.indexOf(prayer.name)
            if (index < 0) return@prayers emptyList()
            val time = runCatching { LocalTime.parse(prayer.time) }.getOrNull() ?: return@prayers emptyList()
            val at = date.atTime(time).atZone(zone).toInstant()
            listOf(false, true).mapNotNull { atPrayer ->
                val trigger = if (atPrayer) at else at.minusSeconds(15 * 60)
                if (!trigger.isAfter(now)) null else AdhanAlarm(
                    40000 + Math.floorMod(date.toEpochDay(), 8).toInt() * 10 + index * 2 + if (atPrayer) 1 else 0,
                    prayer.name, atPrayer, trigger.toEpochMilli(),
                )
            }
        }
    }
}
