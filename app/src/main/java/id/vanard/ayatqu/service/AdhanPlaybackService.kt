package id.vanard.ayatqu.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import id.vanard.ayatqu.data.AdhanPreference
import id.vanard.ayatqu.util.AdhanAudioPlayer
import id.vanard.ayatqu.util.NotificationHelper
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first

class AdhanPlaybackService : Service() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private lateinit var audio: AdhanAudioPlayer
    private var playbackJob: Job? = null
    private var prayerName = ""

    override fun onCreate() { super.onCreate(); audio = AdhanAudioPlayer(this) }
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP || intent == null) {
            finishPlayback()
            return START_NOT_STICKY
        }
        prayerName = intent.getStringExtra(NotificationHelper.EXTRA_PRAYER_NAME).orEmpty()
        startForeground(NotificationHelper.NOTIFICATION_ID_ADHAN,
            NotificationHelper.adhanNotification(this, prayerName, true))
        playbackJob?.cancel()
        audio.stop()
        playbackJob = scope.launch {
            val prefs = AdhanPreference(this@AdhanPlaybackService)
            if (!prefs.notificationsEnabled.first()) { finishPlayback(); return@launch }
            val type = prefs.adhanSoundType.first()
            audio.play(type, alarm = true, onFinished = ::finishPlayback, onError = ::finishPlayback)
            // Bound the service lifetime even if a device fails to deliver a media callback.
            delay(10 * 60 * 1000L)
            finishPlayback()
        }
        return START_NOT_STICKY
    }

    private fun finishPlayback() {
        audio.stop()
        stopForeground(STOP_FOREGROUND_REMOVE)
        if (prayerName.isNotEmpty()) NotificationHelper.showAdhanNotification(this, prayerName)
        stopSelf()
    }

    override fun onDestroy() { scope.cancel(); audio.stop(); super.onDestroy() }
    companion object { const val ACTION_STOP = "stop_adhan" }
}
