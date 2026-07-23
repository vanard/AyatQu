package id.vanard.ayatqu.data.repository

import id.vanard.ayatqu.data.LastReadPreference
import id.vanard.ayatqu.data.local.SurahDetailLocalCache
import id.vanard.ayatqu.data.local.SurahLocalCache
import id.vanard.ayatqu.data.remote.QuranApiService
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
    private val surahLocalCache: SurahLocalCache,
    private val surahDetailLocalCache: SurahDetailLocalCache,
) : QuranRepository {

    override suspend fun getSurahs(): Result<List<Surah>> = withContext(Dispatchers.IO) {
        runCatching {
            val cached = surahLocalCache.read()
            if (cached != null) {
                return@runCatching cached.map(::toDomain)
            }

            if (!networkUtils.isNetworkAvailable()) {
                throw IOException("No internet connection and no cached data available.")
            }

            val surahs = api.getSurahs().data.surahs
            surahLocalCache.write(surahs)
            surahs.map(::toDomain)
        }
    }

    /**
     * Fetch surah detail with verses and translations.
     * Audio URLs are NOT fetched here — use getAyahAudioUrl() on demand.
     */
    override suspend fun getSurahWithAyahs(surahNumber: Int): Result<Pair<Surah, List<Ayah>>> =
        withContext(Dispatchers.IO) {
            runCatching {
                val cached = surahDetailLocalCache.read(surahNumber)
                if (cached != null) {
                    val surah = toDomain(cached.surah)
                    val ayahs = cached.verses.map { it.toDomain(surahNumber) }
                    return@runCatching surah to ayahs
                }

                if (!networkUtils.isNetworkAvailable()) {
                    throw IOException("No internet connection. Please check your network and try again.")
                }

                val response = api.getSurahWithVerses(surahNumber).data
                val surah = toDomain(response.surah)
                val verses = response.verses.orEmpty()
                val ayahs = verses.map { it.toDomain(surahNumber) }

                val cachedDetail = id.vanard.ayatqu.data.local.CachedSurahDetail(
                    surah = response.surah,
                    verses = verses,
                )
                surahDetailLocalCache.write(surahNumber, cachedDetail)

                surah to ayahs
            }
        }

    /**
     * Fetch audio URL for a single ayah on demand.
     * Returns the audio URL string or null on failure.
     */
    override suspend fun getAyahAudioUrl(surahNumber: Int, ayahNumber: Int): String? =
        withContext(Dispatchers.IO) {
            runCatching {
                api.getAyahAudio(surahNumber, ayahNumber).data.audio
            }.getOrNull()
        }

    override suspend fun getAyah(surahNumber: Int, ayahNumber: Int): Result<Ayah> = safeCall {
        val verseResponse = api.getAyah(surahNumber, ayahNumber).data
        val verse = verseResponse.verse.toDomain(surahNumber)
        val audioResponse = api.getAyahAudio(surahNumber, ayahNumber).data
        val audio = listOf(
            AyahAudio(
                reciterId = 0,
                reciterName = audioResponse.reciter.orEmpty(),
                surahAudioUrl = audioResponse.audio,
                ayahAudioUrl = audioResponse.audio,
            )
        )
        verse.copy(audioUrls = audio)
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
        audioUrls = emptyList(),
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

    // ── Safe call wrapper ─────────────────────────────────────────────────────

    private suspend fun <T> safeCall(block: suspend () -> T): Result<T> =
        withContext(Dispatchers.IO) {
            runCatching {
                if (!networkUtils.isNetworkAvailable()) {
                    throw IOException("No internet connection. Please check your network and try again.")
                }
                block()
            }
        }
}
