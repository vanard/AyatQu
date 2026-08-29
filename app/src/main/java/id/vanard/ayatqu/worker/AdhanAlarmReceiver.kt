package id.vanard.ayatqu.worker

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import id.vanard.ayatqu.R
import id.vanard.ayatqu.data.AdhanPreference
import id.vanard.ayatqu.util.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AdhanAlarmReceiver : BroadcastReceiver() {

    private var mediaPlayer: MediaPlayer? = null
    private var audioFocusRequest: AudioFocusRequest? = null

    override fun onReceive(context: Context, intent: Intent) {
        val prayerName = intent.getStringExtra(AdhanSchedulerWorker.EXTRA_PRAYER_NAME) ?: return
        val isAdzanTime = intent.getBooleanExtra(AdhanSchedulerWorker.EXTRA_IS_ADZAN_TIME, false)

        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val preferences = AdhanPreference(context)
                val notificationsEnabled = preferences.notificationsEnabled.first()
                if (!notificationsEnabled) return@launch

                if (isAdzanTime) {
                    val soundType = preferences.adhanSoundType.first()

                    if (isDndActive(context) && soundType != AdhanPreference.SOUND_TYPE_SILENT) {
                        NotificationHelper.showAdhanNotification(context, prayerName, AdhanPreference.SOUND_TYPE_SILENT)
                    } else {
                        NotificationHelper.showAdhanNotification(context, prayerName, soundType)
                        if (soundType != AdhanPreference.SOUND_TYPE_SILENT) {
                            playAdzanSound(context, soundType)
                        }
                    }
                } else {
                    if (!isDndActive(context)) {
                        NotificationHelper.showPreAdhanNotification(context, prayerName)
                    }
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun isDndActive(context: Context): Boolean {
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            notificationManager.currentInterruptionFilter != NotificationManager.INTERRUPTION_FILTER_ALL
        } else {
            false
        }
    }

    private fun playAdzanSound(context: Context, soundType: String) {
        try {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

            val audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()

            val focusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
                when (focusChange) {
                    AudioManager.AUDIOFOCUS_LOSS -> {
                        stopPlayback()
                    }
                    AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                        mediaPlayer?.pause()
                    }
                    AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                        mediaPlayer?.setVolume(0.3f, 0.3f)
                    }
                    AudioManager.AUDIOFOCUS_GAIN -> {
                        mediaPlayer?.setVolume(1f, 1f)
                        mediaPlayer?.start()
                    }
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setAudioAttributes(audioAttributes)
                    .setOnAudioFocusChangeListener(focusChangeListener)
                    .setAcceptsDelayedFocusGain(true)
                    .build()
                audioManager.requestAudioFocus(audioFocusRequest!!)
            } else {
                @Suppress("DEPRECATION")
                audioManager.requestAudioFocus(
                    focusChangeListener,
                    AudioManager.STREAM_ALARM,
                    AudioManager.AUDIOFOCUS_GAIN
                )
            }

            val rawResId = AdhanPreference.getRawResId(soundType)
            val uri = android.net.Uri.parse(
                "android.resource://${context.packageName}/$rawResId"
            )
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(audioAttributes)
                setDataSource(context, uri)
                setOnPreparedListener { start() }
                setOnCompletionListener { mp ->
                    abandonAudioFocus(context)
                    mp.release()
                    mediaPlayer = null
                }
                setOnErrorListener { mp, _, _ ->
                    abandonAudioFocus(context)
                    mp.release()
                    mediaPlayer = null
                    true
                }
                prepareAsync()
            }
        } catch (_: Exception) {
            abandonAudioFocus(context)
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    private fun abandonAudioFocus(context: Context) {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest?.let { audioManager.abandonAudioFocusRequest(it) }
        } else {
            @Suppress("DEPRECATION")
            audioManager.abandonAudioFocus(null)
        }
    }

    private fun stopPlayback() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
