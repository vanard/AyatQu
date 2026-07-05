package id.vanard.ayatqu.domain.repository

import id.vanard.ayatqu.domain.model.Ayah
import id.vanard.ayatqu.domain.model.Surah

interface QuranRepository {
    suspend fun getSurahs(): Result<List<Surah>>
    suspend fun getSurahWithAyahs(surahNumber: Int): Result<Pair<Surah, List<Ayah>>>
    suspend fun getAyah(surahNumber: Int, ayahNumber: Int): Result<Ayah>
}
