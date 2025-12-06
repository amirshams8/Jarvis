package com.jarvis.assistant.modes

import android.content.Context
import android.media.AudioManager
import android.view.KeyEvent
import com.jarvis.assistant.utils.Logger

class MediaController(private val context: Context) {
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    fun playPause() {
        sendMediaKey(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE)
        Logger.log("🎵 Media: Play/Pause", Logger.Level.INFO)
    }

    fun next() {
        sendMediaKey(KeyEvent.KEYCODE_MEDIA_NEXT)
        Logger.log("⏭️ Media: Next", Logger.Level.INFO)
    }

    fun previous() {
        sendMediaKey(KeyEvent.KEYCODE_MEDIA_PREVIOUS)
        Logger.log("⏮️ Media: Previous", Logger.Level.INFO)
    }

    fun volumeUp() {
        audioManager.adjustStreamVolume(
            AudioManager.STREAM_MUSIC,
            AudioManager.ADJUST_RAISE,
            0
        )
    }

    fun volumeDown() {
        audioManager.adjustStreamVolume(
            AudioManager.STREAM_MUSIC,
            AudioManager.ADJUST_LOWER,
            0
        )
    }

    private fun sendMediaKey(keyCode: Int) {
        val event = KeyEvent(KeyEvent.ACTION_DOWN, keyCode)
        audioManager.dispatchMediaKeyEvent(event)
    }
}
