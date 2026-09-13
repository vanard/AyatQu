package id.vanard.ayatqu.data.local

import android.content.Context
import com.google.gson.Gson
import id.vanard.ayatqu.domain.model.Juz
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class JuzLocalCache(context: Context) {
    private val cacheDir = File(context.filesDir, "juz_details").also { it.mkdirs() }
    private val gson = Gson()

    suspend fun read(juzNumber: Int): Juz? = withContext(Dispatchers.IO) {
        val file = File(cacheDir, "juz_$juzNumber.json")
        if (!file.exists()) return@withContext null
        runCatching { gson.fromJson(file.readText(), Juz::class.java) }.getOrNull()
    }

    suspend fun write(juz: Juz) = withContext(Dispatchers.IO) {
        runCatching {
            File(cacheDir, "juz_${juz.number}.json").writeText(gson.toJson(juz))
        }
    }

    suspend fun clear() = withContext(Dispatchers.IO) {
        cacheDir.listFiles()?.forEach(File::delete)
    }
}
