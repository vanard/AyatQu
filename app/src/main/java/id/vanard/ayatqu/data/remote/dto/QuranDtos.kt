package id.vanard.ayatqu.data.remote.dto

import com.google.gson.annotations.SerializedName

// ── Surah list ────────────────────────────────────────────────────────────────

data class SurahListResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: SurahListData,
)

data class SurahListData(
    @SerializedName("total") val total: Int,
    @SerializedName("surahs") val surahs: List<SurahDto>,
)

data class SurahDto(
    @SerializedName("number") val number: Int,
    @SerializedName("name_arabic") val nameArabic: String,
    @SerializedName("name_english") val nameEnglish: String,
    @SerializedName("name_translation") val nameTranslation: String,
    @SerializedName("revelation_place") val revelationPlace: String,
    @SerializedName("verses_count") val versesCount: Int,
    @SerializedName("bismillah_pre") val bismillahPre: Boolean,
)

// ── Surah detail (with verses) ────────────────────────────────────────────────

data class SurahDetailResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: SurahDetailData,
)

data class SurahDetailData(
    @SerializedName("surah") val surah: SurahDto,
    @SerializedName("verses") val verses: List<VerseDto>?,
    @SerializedName("total_verses") val totalVerses: Int?,
    @SerializedName("audio") val audio: List<SurahAudioDto>,
)

data class VerseDto(
    @SerializedName("verse_key") val verseKey: String,
    @SerializedName("ayah") val ayah: Int,
    @SerializedName("arabic") val arabic: String,
    @SerializedName("transliteration") val transliteration: String?,
    @SerializedName("translations") val translations: TranslationsDto?,
)

// ── Juz detail ───────────────────────────────────────────────────────────────

data class JuzResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: JuzData,
)

data class JuzData(
    @SerializedName("juz_number") val juzNumber: Int,
    @SerializedName("total_verses") val totalVerses: Int,
    @SerializedName("verses") val verses: List<JuzVerseDto>,
)

data class JuzVerseDto(
    @SerializedName("verse_key") val verseKey: String,
    @SerializedName("surah_name") val surahName: String,
    @SerializedName("ayah") val ayah: Int,
    @SerializedName("arabic") val arabic: String,
    @SerializedName("transliteration") val transliteration: String?,
    @SerializedName("translations") val translations: TranslationsDto?,
)

data class TranslationsDto(
    @SerializedName("sahih_international") val sahihInternational: String?,
    @SerializedName("indonesian") val indonesian: String?,
    @SerializedName("malay") val malay: String?,
    @SerializedName("urdu") val urdu: String?,
    @SerializedName("turkish") val turkish: String?,
    @SerializedName("french") val french: String?,
    @SerializedName("german") val german: String?,
    @SerializedName("bengali") val bengali: String?,
    @SerializedName("spanish") val spanish: String?,
    @SerializedName("ayah_audio") val ayahAudio: String?,
)

// ── Single ayah ───────────────────────────────────────────────────────────────

data class AyahResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: AyahData,
)

data class AyahData(
    @SerializedName("surah") val surah: SurahDto,
    @SerializedName("verse") val verse: VerseDto,
    @SerializedName("audio") val audio: List<AyahAudioDto>,
)

data class SurahAudioDto(
    @SerializedName("reciter_id") val reciterId: Int,
    @SerializedName("reciter") val reciter: String,
    @SerializedName("style") val style: String,
    @SerializedName("surah_audio") val surahAudio: String,
)

data class AyahAudioDto(
    @SerializedName("reciter_id") val reciterId: Int,
    @SerializedName("reciter") val reciter: String,
    @SerializedName("style") val style: String,
    @SerializedName("surah_audio") val surahAudio: String,
    @SerializedName("ayah_audio") val ayahAudio: String?,
)

// ── Lightweight audio endpoint ────────────────────────────────────────────────

data class AudioResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("data") val data: AudioData,
)

data class AudioData(
    @SerializedName("audio") val audio: String?,
    @SerializedName("reciter") val reciter: String?,
    @SerializedName("reciters") val reciters: List<AudioReciterDto>?,
) {
    fun firstAvailableAudioUrl(): String? =
        reciters.orEmpty()
            .firstNotNullOfOrNull { reciter -> reciter.audioUrl?.takeIf(String::isNotBlank) }
            ?: audio?.takeIf(String::isNotBlank)
}

data class AudioReciterDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("name_arabic") val nameArabic: String?,
    @SerializedName("style") val style: String?,
    @SerializedName("audio_url") val audioUrl: String?,
)
