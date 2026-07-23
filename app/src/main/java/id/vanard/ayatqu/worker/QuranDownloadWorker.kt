package id.vanard.ayatqu.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import id.vanard.ayatqu.R
import id.vanard.ayatqu.data.local.AyahAudioCache
import id.vanard.ayatqu.data.remote.QuranApiService
import org.koin.java.KoinJavaComponent.inject
import java.net.URL

class QuranDownloadWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params) {

    private val api: QuranApiService by inject(QuranApiService::class.java)
    private val audioCache: AyahAudioCache by inject(AyahAudioCache::class.java)

    override suspend fun doWork(): Result {
        val surahNumber = inputData.getInt(KEY_SURAH_NUMBER, -1)
        if (surahNumber < 0) return Result.failure()

        val notificationManager = getNotificationManager()
        createNotificationChannel(notificationManager)

        return try {
            val response = api.getSurahWithVerses(surahNumber).data
            val verses = response.verses.orEmpty()
            val audioDtos = response.audio
            val total = verses.size

            verses.forEachIndexed { index, verse ->
                // Find audio URL from surah-level audio list
                val audioUrl = audioDtos.firstOrNull()?.surahAudio
                    ?: return@forEachIndexed

                // Build per-ayah URL by appending ayah number if the API supports it
                // Fallback: download the full surah audio on first ayah only
                val ayahAudioUrl = if (index == 0) audioUrl else return@forEachIndexed

                if (audioCache.isDownloaded(surahNumber, verse.ayah)) return@forEachIndexed

                val file = audioCache.getAyahFile(surahNumber, verse.ayah)
                URL(ayahAudioUrl).openStream().use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                audioCache.markDownloaded(surahNumber, verse.ayah, file.absolutePath)

                // Update progress notification
                val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_home)
                    .setContentTitle("Downloading Surah $surahNumber")
                    .setContentText("${index + 1}/$total ayahs")
                    .setProgress(total, index + 1, false)
                    .setSilent(true)
                    .setOngoing(true)
                    .build()

                notificationManager.notify(NOTIFICATION_ID, notification)
            }

            // Completion notification
            val done = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_home)
                .setContentTitle("Download complete")
                .setContentText("Surah $surahNumber is ready for offline listening")
                .setAutoCancel(true)
                .build()
            notificationManager.notify(NOTIFICATION_ID, done)

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    private fun getNotificationManager(): NotificationManager {
        return applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    private fun createNotificationChannel(manager: NotificationManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Quran Downloads",
                NotificationManager.IMPORTANCE_LOW,
            ).apply {
                description = "Shows download progress for Quran audio"
            }
            manager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val WORK_NAME = "quran_download"
        const val KEY_SURAH_NUMBER = "surah_number"
        private const val CHANNEL_ID = "quran_download_channel"
        private const val NOTIFICATION_ID = 1001

        fun createRequest(surahNumber: Int): Data {
            return Data.Builder()
                .putInt(KEY_SURAH_NUMBER, surahNumber)
                .build()
        }
    }
}
