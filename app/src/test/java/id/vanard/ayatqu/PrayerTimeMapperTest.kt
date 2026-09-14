package id.vanard.ayatqu

import id.vanard.ayatqu.data.remote.dto.PrayerTimingsDto
import id.vanard.ayatqu.data.repository.toPrayerTimes
import org.junit.Assert.assertEquals
import org.junit.Test

class PrayerTimeMapperTest {
    @Test
    fun includesImsakBeforeFajrAndCleansTimezoneSuffix() {
        val prayerTimes = timings(imsak = "04:22 (WIB)").toPrayerTimes()

        assertEquals("Imsak", prayerTimes[0].name)
        assertEquals("04:22", prayerTimes[0].time)
        assertEquals("Fajr", prayerTimes[1].name)
    }

    @Test
    fun omitsImsakWhenApiDoesNotProvideIt() {
        val prayerTimes = timings(imsak = null).toPrayerTimes()

        assertEquals(listOf("Fajr", "Sunrise", "Dhuhr", "Asr", "Maghrib", "Isha"), prayerTimes.map { it.name })
    }

    private fun timings(imsak: String?) = PrayerTimingsDto(
        fajr = "04:32",
        sunrise = "05:58",
        dhuhr = "12:05",
        asr = "15:28",
        sunset = "18:12",
        maghrib = "18:12",
        isha = "19:24",
        imsak = imsak,
        midnight = "00:05",
    )
}
