package com.jarvis.assistant.media

import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.view.KeyEvent

class MediaController(private val context: Context) {
    
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val mediaSessionManager = context.getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
    
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
    
    fun volumeUp() {
        audioManager.adjustStreamVolume(
            AudioManager.STREAM_MUSIC,
            AudioManager.ADJUST_RAISE,
            AudioManager.FLAG_SHOW_UI
        )
    }
    
    fun volumeDown() {
        audioManager.adjustStreamVolume(
            AudioManager.STREAM_MUSIC,
            AudioManager.ADJUST_LOWER,
            AudioManager.FLAG_SHOW_UI
        )
    }
    
    fun setVolume(percent: Int) {
        val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        val targetVolume = (maxVolume * percent / 100).coerceIn(0, maxVolume)
        
        audioManager.setStreamVolume(
            AudioManager.STREAM_MUSIC,
            targetVolume,
            AudioManager.FLAG_SHOW_UI
        )
        
        JarvisCore.speak("Volume set to $percent percent")
    }
    
    private fun sendMediaButton(keyCode: Int) {
        val downEvent = KeyEvent(KeyEvent.ACTION_DOWN, keyCode)
        val upEvent = KeyEvent(KeyEvent.ACTION_UP, keyCode)
        
        audioManager.dispatchMediaKeyEvent(downEvent)
        audioManager.dispatchMediaKeyEvent(upEvent)
    }
    
    fun playSpotify(query: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = android.net.Uri.parse("spotify:search:$query")
            setPackage(Constants.SPOTIFY_PKG)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
    
    fun playYouTubeMusic(query: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = android.net.Uri.parse("https://music.youtube.com/search?q=$query")
            setPackage(Constants.YT_MUSIC_PKG)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
    
    fun playJioSaavn(query: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = android.net.Uri.parse("jiosaavn://search?query=$query")
            setPackage(Constants.JIOSAAVN_PKG)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
    
    fun getCurrentlyPlaying(): String? {
        val controllers = mediaSessionManager.getActiveSessions(null)
        return controllers.firstOrNull()?.metadata?.description?.title?.toString()
    }
}