package id.vanard.ayatqu.domain.repository

import id.vanard.ayatqu.domain.model.Ayah
import id.vanard.ayatqu.domain.model.LastRead
import id.vanard.ayatqu.domain.model.Surah
import kotlinx.coroutines.flow.Flow

interface QuranRepository {
    suspend fun getSurahs(): Result<List<Surah>>
    suspend fun getSurahWithAyahs(surahNumber: Int): Result<Pair<Surah, List<Ayah>>>
    suspend fun getAyah(surahNumber: Int, ayahNumber: Int): Result<Ayah>
    suspend fun getAyahAudioUrl(surahNumber: Int, ayahNumber: Int): String?

    // Last read
    fun getLastRead(): Flow<LastRead?>
    suspend fun saveLastRead(lastRead: LastRead)
}
