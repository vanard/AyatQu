package id.vanard.ayatqu.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class QuranDownloadWorker(
    appContext: Context,
    params: WorkerParameters,
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        // TODO: implement Quran download logic
        return Result.success()
    }

    companion object {
        const val WORK_NAME = "quran_download"
    }
}
