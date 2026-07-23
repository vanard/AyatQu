package id.vanard.ayatqu.data.remote

import id.vanard.ayatqu.data.remote.dto.AyahResponse
import id.vanard.ayatqu.data.remote.dto.AudioResponse
import id.vanard.ayatqu.data.remote.dto.SurahDetailResponse
import id.vanard.ayatqu.data.remote.dto.SurahListResponse
import retrofit2.http.GET
import retrofit2.http.Path

interface QuranApiService {

    /** Get all 114 surahs */
    @GET("quran/surahs")
    suspend fun getSurahs(): SurahListResponse

    /**
     * Get surah detail with all verses and translations.
     * Requires API key in Authorization header (set via OkHttp interceptor).
     */
    @GET("quran/surah/{surahNumber}")
    suspend fun getSurahWithVerses(
        @Path("surahNumber") surahNumber: Int,
    ): SurahDetailResponse

    /** Get a single ayah with translations and audio */
    @GET("quran/surah/{surahNumber}/ayah/{ayahNumber}")
    suspend fun getAyah(
        @Path("surahNumber") surahNumber: Int,
        @Path("ayahNumber") ayahNumber: Int,
    ): AyahResponse

    /** Get audio URL for a specific ayah (lightweight — no verse/translation data) */
    @GET("quran/audio/{surahNumber}/{ayahNumber}")
    suspend fun getAyahAudio(
        @Path("surahNumber") surahNumber: Int,
        @Path("ayahNumber") ayahNumber: Int,
    ): AudioResponse
}
