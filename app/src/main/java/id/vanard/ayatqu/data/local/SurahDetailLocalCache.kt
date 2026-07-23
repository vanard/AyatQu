package id.vanard.ayatqu.data.local

import android.content.Context
import com.google.gson.Gson
import id.vanard.ayatqu.data.remote.dto.AudioData
import id.vanard.ayatqu.data.remote.dto.SurahDto
import id.vanard.ayatqu.data.remote.dto.VerseDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

data class CachedSurahDetail(
    val surah: SurahDto,
    val verses: List<VerseDto>,
    val ayahAudios: Map<Int, AudioData>,
)

class SurahDetailLocalCache(context: Context) {

    private val cacheDir = File(context.filesDir, "surah_details").also { it.mkdirs() }
    private val gson = Gson()

    suspend fun read(surahNumber: Int): CachedSurahDetail? = withContext(Dispatchers.IO) {
        val file = File(cacheDir, "surah_$surahNumber.json")
        if (!file.exists()) return@withContext null

        runCatching {
            gson.fromJson(file.readText(), CachedSurahDetail::class.java)
        }.getOrNull()
    }

    suspend fun write(surahNumber: Int, data: CachedSurahDetail) = withContext(Dispatchers.IO) {
        runCatching {
            val file = File(cacheDir, "surah_$surahNumber.json")
            file.writeText(gson.toJson(data))
        }
    }

    suspend fun hasCache(surahNumber: Int): Boolean = withContext(Dispatchers.IO) {
        val file = File(cacheDir, "surah_$surahNumber.json")
        file.exists() && file.length() > 0
    }
}
