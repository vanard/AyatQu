package id.vanard.ayatqu.data.local

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import id.vanard.ayatqu.data.remote.dto.SurahDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Local cache for the surah list.
 * Stores the 114 surahs as JSON in internal storage.
 * Since surah data is static, we cache it once and never re-fetch.
 */
class SurahLocalCache(context: Context) {

    private val cacheFile = File(context.filesDir, CACHE_FILE_NAME)
    private val gson = Gson()

    companion object {
        private const val CACHE_FILE_NAME = "surah_list_cache.json"
    }

    /**
     * Read cached surah list from disk.
     * Returns null if cache doesn't exist or is corrupted.
     */
    suspend fun read(): List<SurahDto>? = withContext(Dispatchers.IO) {
        if (!cacheFile.exists()) return@withContext null

        runCatching {
            val json = cacheFile.readText()
            val type = object : TypeToken<List<SurahDto>>() {}.type
            gson.fromJson<List<SurahDto>>(json, type)
        }.getOrNull()
    }

    /**
     * Write surah list to disk cache.
     */
    suspend fun write(surahs: List<SurahDto>) = withContext(Dispatchers.IO) {
        runCatching {
            val json = gson.toJson(surahs)
            cacheFile.writeText(json)
        }
    }

    /**
     * Check if cache has data.
     */
    suspend fun hasCache(): Boolean = withContext(Dispatchers.IO) {
        cacheFile.exists() && cacheFile.length() > 0
    }

    /**
     * Clear the cache (useful for testing or force-refresh).
     */
    suspend fun clear() = withContext(Dispatchers.IO) {
        if (cacheFile.exists()) {
            cacheFile.delete()
        }
    }
}
