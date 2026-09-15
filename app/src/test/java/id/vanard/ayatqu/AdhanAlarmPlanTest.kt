package id.vanard.ayatqu

import id.vanard.ayatqu.data.decodePrayerTimes
import id.vanard.ayatqu.domain.model.AdhanAlarmPlan
import id.vanard.ayatqu.domain.model.AdhanDay
import id.vanard.ayatqu.domain.model.PrayerTime
import org.junit.Assert.*
import org.junit.Test
import java.time.Instant
import java.time.LocalDate

class AdhanAlarmPlanTest {
    @Test fun cachePreservesHourAndMinuteAndIgnoresMalformedEntries() {
        assertEquals(listOf(PrayerTime("Fajr", "04:32"), PrayerTime("Dhuhr", "12:00")),
            decodePrayerTimes("Fajr:04:32;broken;Dhuhr:12:00;Asr:25:99"))
    }

    @Test fun reminderIsFifteenMinutesBeforePrayerInLocationTimezone() {
        val alarms = AdhanAlarmPlan.upcoming(listOf(AdhanDay("2026-09-15", "Asia/Jakarta",
            listOf(PrayerTime("Dhuhr", "12:00"), PrayerTime("Sunrise", "06:00")))), Instant.parse("2026-09-15T00:00:00Z"))
        assertEquals(2, alarms.size)
        assertEquals(Instant.parse("2026-09-15T04:45:00Z").toEpochMilli(), alarms[0].triggerMillis)
        assertEquals(15 * 60 * 1000L, alarms[1].triggerMillis - alarms[0].triggerMillis)
        assertFalse(alarms[0].atPrayerTime)
        assertTrue(alarms[1].atPrayerTime)
    }

    @Test fun missedReminderDoesNotDropUpcomingPrayerOrReplayPastPrayers() {
        val day = AdhanDay("2026-09-15", "UTC", listOf(PrayerTime("Fajr", "05:00"), PrayerTime("Dhuhr", "12:00")))
        val alarms = AdhanAlarmPlan.upcoming(listOf(day), Instant.parse("2026-09-15T11:50:00Z"))
        assertEquals(1, alarms.size)
        assertEquals("Dhuhr", alarms.single().prayer)
        assertTrue(alarms.single().atPrayerTime)
    }

    @Test fun sevenDaysHaveUniqueStableAlarmIdentities() {
        val days = (0L..6L).map { offset -> AdhanDay(LocalDate.of(2026, 12, 29).plusDays(offset).toString(), "UTC",
            AdhanAlarmPlan.prayers.map { PrayerTime(it, "12:00") }) }
        val alarms = AdhanAlarmPlan.upcoming(days, Instant.parse("2026-12-28T00:00:00Z"))
        assertEquals(70, alarms.size)
        assertEquals(70, alarms.map { it.requestCode }.distinct().size)
        assertEquals(alarms, AdhanAlarmPlan.upcoming(days, Instant.parse("2026-12-28T01:00:00Z")))
    }

    @Test fun reminderCanFallOnPreviousDateAndInvalidDataIsIgnored() {
        val alarms = AdhanAlarmPlan.upcoming(listOf(AdhanDay("2026-09-16", "UTC",
            listOf(PrayerTime("Fajr", "00:05"), PrayerTime("Asr", "99:99")))), Instant.parse("2026-09-15T23:00:00Z"))
        assertEquals(Instant.parse("2026-09-15T23:50:00Z").toEpochMilli(), alarms.first().triggerMillis)
        assertEquals(2, alarms.size)
    }
}
