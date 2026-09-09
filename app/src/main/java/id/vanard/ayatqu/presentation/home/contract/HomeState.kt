package id.vanard.ayatqu.presentation.home.contract

import id.vanard.ayatqu.domain.model.LastRead
import id.vanard.ayatqu.domain.model.PrayerTime

data class HomeState(
    val userName: String = "Guest",
    val lastRead: LastRead? = null,
    val prayerTimes: List<PrayerTime> = emptyList(),
    val isPrayerTimesLoading: Boolean = false,
    val prayerTimesError: String? = null,
    val timezone: String? = null,
    val isLocationLoading: Boolean = false,
    val locationError: String? = null,
    val isNetworkAvailable: Boolean = true,
)
