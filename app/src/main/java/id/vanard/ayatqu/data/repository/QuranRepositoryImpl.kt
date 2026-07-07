package id.vanard.ayatqu.data.repository

import id.vanard.ayatqu.data.LastReadPreference
import id.vanard.ayatqu.data.remote.QuranApiService
import id.vanard.ayatqu.data.remote.dto.AyahAudioDto
import id.vanard.ayatqu.data.remote.dto.SurahAudioDto
import id.vanard.ayatqu.data.remote.dto.SurahDetailData
import id.vanard.ayatqu.data.remote.dto.TranslationsDto
import id.vanard.ayatqu.data.remote.dto.VerseDto
import id.vanard.ayatqu.domain.model.Ayah
import id.vanard.ayatqu.domain.model.AyahAudio
import id.vanard.ayatqu.domain.model.LastRead
import id.vanard.ayatqu.domain.model.Surah
import id.vanard.ayatqu.domain.repository.QuranRepository
import id.vanard.ayatqu.util.NetworkUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.IOException

class QuranRepositoryImpl(
    private val api: QuranApiService,
    private val networkUtils: NetworkUtils,
    private val lastReadPreference: LastReadPreference,
) : QuranRepository {

    override suspend fun getSurahs(): Result<List<Surah>> = safeCall {
        api.getSurahs()
            .data
            .surahs
            .map(::toDomain)
    }

    override suspend fun getSurahWithAyahs(surahNumber: Int): Result<Pair<Surah, List<Ayah>>> =
        safeCall {
            val response = api.getSurahWithVerses(surahNumber).data
            val surah = toDomain(response.surah)
            val verses = response.verses.orEmpty()
            val ayahs = verses.map { it.toDomain(response.totalVerses ?: verses.size) }
            surah to ayahs
        }

    override suspend fun getAyah(surahNumber: Int, ayahNumber: Int): Result<Ayah> = safeCall {
        val response = api.getAyah(surahNumber, ayahNumber).data
        response.verse.toDomain(surahNumber = response.surah.number)
    }

    // ── Last Read ─────────────────────────────────────────────────────────────

    override fun getLastRead(): Flow<LastRead?> = lastReadPreference.lastRead

    override suspend fun saveLastRead(lastRead: LastRead) {
        lastReadPreference.saveLastRead(lastRead)
    }

    // ── Mapping helpers ───────────────────────────────────────────────────────

    private fun toDomain(dto: id.vanard.ayatqu.data.remote.dto.SurahDto): Surah = Surah(
        number = dto.number,
        nameArabic = dto.nameArabic,
        nameEnglish = dto.nameEnglish,
        nameTranslation = dto.nameTranslation,
        revelationPlace = dto.revelationPlace,
        versesCount = dto.versesCount,
        bismillahPre = dto.bismillahPre,
    )

    private fun VerseDto.toDomain(surahNumber: Int): Ayah = Ayah(
        surahNumber = surahNumber,
        ayahNumber = ayah,
        verseKey = verseKey,
        arabic = arabic,
        transliteration = transliteration.orEmpty(),
        translations = translations?.toTranslationMap().orEmpty(),
        audioUrls = emptyList(), // populated on detail AudioDto if present
    )

    private fun TranslationsDto.toTranslationMap(): Map<String, String> = buildMap {
        sahihInternational?.takeIf { it.isNotBlank() }?.let { put("en", it) }
        indonesian?.takeIf { it.isNotBlank() }?.let { put("id", it) }
        malay?.takeIf { it.isNotBlank() }?.let { put("ms", it) }
        urdu?.takeIf { it.isNotBlank() }?.let { put("ur", it) }
        turkish?.takeIf { it.isNotBlank() }?.let { put("tr", it) }
        french?.takeIf { it.isNotBlank() }?.let { put("fr", it) }
        german?.takeIf { it.isNotBlank() }?.let { put("de", it) }
        bengali?.takeIf { it.isNotBlank() }?.let { put("bn", it) }
        spanish?.takeIf { it.isNotBlank() }?.let { put("es", it) }
    }

    private fun AyahAudioDto.toAyahAudio(): AyahAudio = AyahAudio(
        reciterId = reciterId,
        reciterName = reciter,
        surahAudioUrl = surahAudio,
        ayahAudioUrl = ayahAudio,
    )

    private fun SurahAudioDto.toAyahAudio(): AyahAudio = AyahAudio(
        reciterId = reciterId,
        reciterName = reciter,
        surahAudioUrl = surahAudio,
        ayahAudioUrl = null,
    )

    // ── Safe call wrapper ──────────────────────────────────────────────────────

    private suspend fun <T> safeCall(block: suspend () -> T): Result<T> =
        withContext(Dispatchers.IO) {
            runCatching {
                if (!networkUtils.isNetworkAvailable()) {
                    throw IOException("No internet connection. Please check your network and try again.")
                }
                block()
            }
        }

    @Suppress("unused")
    private fun SurahDetailData.audioAsAyahAudio(): List<AyahAudio> =
        audio.map { it.toAyahAudio() }
}
