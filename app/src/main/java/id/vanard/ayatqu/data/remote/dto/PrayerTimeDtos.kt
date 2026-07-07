package id.vanard.ayatqu.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PrayerTimeResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("status") val status: Boolean,
    @SerializedName("data") val data: PrayerTimeData,
)

data class PrayerTimeData(
    @SerializedName("timings") val timings: PrayerTimingsDto,
    @SerializedName("date") val date: PrayerDateDto,
    @SerializedName("meta") val meta: PrayerMetaDto?,
)

data class PrayerTimingsDto(
    @SerializedName("Fajr") val fajr: String,
    @SerializedName("Sunrise") val sunrise: String,
    @SerializedName("Dhuhr") val dhuhr: String,
    @SerializedName("Asr") val asr: String,
    @SerializedName("Sunset") val sunset: String?,
    @SerializedName("Maghrib") val maghrib: String,
    @SerializedName("Isha") val isha: String,
    @SerializedName("Imsak") val imsak: String?,
    @SerializedName("Midnight") val midnight: String?,
)

data class PrayerDateDto(
    @SerializedName("readable") val readable: String,
    @SerializedName("timestamp") val timestamp: String,
    @SerializedName("gregorian") val gregorian: PrayerCalendarDto?,
    @SerializedName("hijri") val hijri: PrayerHijriDto?,
)

data class PrayerCalendarDto(
    @SerializedName("date") val date: String,
    @SerializedName("format") val format: String,
    @SerializedName("day") val day: String,
    @SerializedName("weekday") val weekday: PrayerWeekdayDto?,
    @SerializedName("month") val month: PrayerMonthDto?,
    @SerializedName("year") val year: String,
)

data class PrayerWeekdayDto(
    @SerializedName("en") val en: String,
)

data class PrayerMonthDto(
    @SerializedName("number") val number: Int,
    @SerializedName("en") val en: String,
)

data class PrayerHijriDto(
    @SerializedName("date") val date: String,
    @SerializedName("format") val format: String,
    @SerializedName("day") val day: String,
    @SerializedName("month") val month: PrayerHijriMonthDto?,
    @SerializedName("year") val year: String,
)

data class PrayerHijriMonthDto(
    @SerializedName("number") val number: Int,
    @SerializedName("en") val en: String,
    @SerializedName("ar") val ar: String,
)

data class PrayerMetaDto(
    @SerializedName("latitude") val latitude: Double?,
    @SerializedName("longitude") val longitude: Double?,
    @SerializedName("timezone") val timezone: String?,
    @SerializedName("method") val method: PrayerMethodDto?,
)

data class PrayerMethodDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
)
