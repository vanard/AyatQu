package id.vanard.ayatqu.util

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import id.vanard.ayatqu.data.AdhanPreference

/** Owned by the visible sound picker or the playback service; never by a receiver. */
class AdhanAudioPlayer(private val context: Context) {
    private var player: MediaPlayer? = null
    private var focus: AudioFocusRequest? = null
    private val manager = context.getSystemService(AudioManager::class.java)

    fun play(type: String, alarm: Boolean = false, onStarted: () -> Unit = {}, onFinished: () -> Unit = {}, onError: () -> Unit = {}) {
        stop()
        if (type == AdhanPreference.SOUND_TYPE_SILENT) {
            onStarted()
            onFinished()
            return
        }
        try {
            val attributes = AudioAttributes.Builder()
                .setUsage(if (alarm) AudioAttributes.USAGE_ALARM else AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build()
            val request = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
                .setAudioAttributes(attributes)
                .setOnAudioFocusChangeListener({ change ->
                    if (change == AudioManager.AUDIOFOCUS_LOSS || change == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                        stop()
                        onFinished()
                    }
                }, Handler(Looper.getMainLooper())).build()
            focus = request
            if (manager.requestAudioFocus(request) != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                stop()
                onError()
                return
            }
            val mp = MediaPlayer()
            player = mp
            mp.setAudioAttributes(attributes)
            if (alarm) mp.setWakeMode(context, PowerManager.PARTIAL_WAKE_LOCK)
            mp.setDataSource(context, Uri.parse("android.resource://${context.packageName}/${AdhanPreference.getRawResId(type)}"))
            mp.setOnPreparedListener {
                if (player === it) { it.start(); onStarted() }
            }
            mp.setOnCompletionListener { stop(); onFinished() }
            mp.setOnErrorListener { _, _, _ -> stop(); onError(); true }
            mp.prepareAsync()
        } catch (_: Exception) {
            stop()
            onError()
        }
    }

    fun stop() {
        player?.release()
        player = null
        focus?.let { manager.abandonAudioFocusRequest(it) }
        focus = null
    }
}
