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
