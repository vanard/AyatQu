package id.vanard.ayatqu.domain.model

data class Surah(
    val number: Int,
    val nameArabic: String,
    val nameEnglish: String,
    val nameTranslation: String,
    val revelationPlace: String,
    val versesCount: Int,
    val bismillahPre: Boolean,
)

data class Ayah(
    val surahNumber: Int,
    val ayahNumber: Int,
    val verseKey: String,
    val arabic: String,
    val transliteration: String,
    val translations: Map<String, String>,
    val audioUrls: List<AyahAudio>,
)

data class AyahAudio(
    val reciterId: Int,
    val reciterName: String,
    val surahAudioUrl: String,
    val ayahAudioUrl: String?,
)

data class Juz(
    val number: Int,
    val totalVerses: Int,
    val verses: List<JuzVerse>,
)

data class JuzVerse(
    val surahNumber: Int,
    val surahName: String,
    val ayahNumber: Int,
    val verseKey: String,
    val arabic: String,
    val transliteration: String,
    val translations: Map<String, String>,
)

data class JuzSummary(
    val number: Int,
    val startSurahName: String,
    val startAyah: Int,
    val endSurahName: String,
    val endAyah: Int,
) {
    val rangeLabel: String
        get() = if (startSurahName == endSurahName) {
            "$startSurahName $startAyah–$endAyah"
        } else {
            "$startSurahName $startAyah – $endSurahName $endAyah"
        }
}

data class LastRead(
    val surahNumber: Int,
    val ayahNumber: Int,
    val surahName: String,
    val timestamp: Long = 0L,
)

data class PrayerTime(
    val name: String,
    val time: String,
)

data class PrayerTimesResult(
    val prayerTimes: List<PrayerTime>,
    val timezone: String?,
)
