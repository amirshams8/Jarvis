package com.jarvis.assistant.media

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.view.KeyEvent
import com.jarvis.assistant.core.Constants
import com.jarvis.assistant.core.JarvisCore

class MediaController(private val context: Context) {
    
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    
    fun play() {
        sendMediaButton(KeyEvent.KEYCODE_MEDIA_PLAY)
        JarvisCore.speak("Playing")
    }
    
    fun pause() {
        sendMediaButton(KeyEvent.KEYCODE_MEDIA_PAUSE)
        JarvisCore.speak("Paused")
    }
    
    fun next() {
        sendMediaButton(KeyEvent.KEYCODE_MEDIA_NEXT)
        JarvisCore.speak("Next track")
    }
    
    fun previous() {
        sendMediaButton(KeyEvent.KEYCODE_MEDIA_PREVIOUS)
        JarvisCore.speak("Previous track")
    }
    
    private fun sendMediaButton(keyCode: Int) {
        val downEvent = KeyEvent(KeyEvent.ACTION_DOWN, keyCode)
        val upEvent = KeyEvent(KeyEvent.ACTION_UP, keyCode)
        audioManager.dispatchMediaKeyEvent(downEvent)
        audioManager.dispatchMediaKeyEvent(upEvent)
    }
    
    fun openSpotify() {
        launchMusicApp(Constants.SPOTIFY_PKG, "Spotify")
    }
    
    fun openYouTubeMusic() {
        launchMusicApp(Constants.YT_MUSIC_PKG, "YouTube Music")
    }
    
    fun openJioSaavn() {
        launchMusicApp(Constants.JIOSAAVN_PKG, "JioSaavn")
    }
    
    private fun launchMusicApp(packageName: String, appName: String) {
        try {
            val intent = context.packageManager.getLaunchIntentForPackage(packageName)
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                JarvisCore.speak("Opening $appName")
            } else {
                JarvisCore.speak("$appName not installed")
            }
        } catch (e: Exception) {
            JarvisCore.speak("Failed to open $appName")
        }
    }
}
