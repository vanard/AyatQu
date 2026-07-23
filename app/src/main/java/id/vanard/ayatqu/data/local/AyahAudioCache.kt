package id.vanard.ayatqu.data.local

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

/**
 * Tracks which ayah audio files have been downloaded to local storage.
 * Key: "surahNumber_ayahNumber", Value: local file path.
 */
class AyahAudioCache(context: Context) {

    private val cacheDir = File(context.filesDir, "quran_audio").apply { mkdirs() }
    private val indexFile = File(cacheDir, "index.json")
    private val gson = Gson()

    private var index: MutableMap<String, String> = loadIndex()

    private fun loadIndex(): MutableMap<String, String> {
        if (!indexFile.exists()) return mutableMapOf()
        return runCatching {
            val json = indexFile.readText()
            val type = object : TypeToken<MutableMap<String, String>>() {}.type
            gson.fromJson<MutableMap<String, String>>(json, type)
        }.getOrNull() ?: mutableMapOf()
    }

    private fun saveIndex() {
        indexFile.writeText(gson.toJson(index))
    }

    fun isDownloaded(surahNumber: Int, ayahNumber: Int): Boolean {
        val key = "${surahNumber}_$ayahNumber"
        val path = index[key] ?: return false
        return File(path).exists()
    }

    fun getLocalPath(surahNumber: Int, ayahNumber: Int): String? {
        val key = "${surahNumber}_$ayahNumber"
        val path = index[key] ?: return null
        return if (File(path).exists()) path else null
    }

    fun markDownloaded(surahNumber: Int, ayahNumber: Int, filePath: String) {
        val key = "${surahNumber}_$ayahNumber"
        index[key] = filePath
        saveIndex()
    }

    fun getDownloadedCount(surahNumber: Int, totalAyahs: Int): Int {
        return (1..totalAyahs).count { isDownloaded(surahNumber, it) }
    }

    fun getAudioDir(): File = cacheDir

    fun getAyahFile(surahNumber: Int, ayahNumber: Int): File {
        return File(cacheDir, "${surahNumber}_${ayahNumber}.mp3")
    }
}
