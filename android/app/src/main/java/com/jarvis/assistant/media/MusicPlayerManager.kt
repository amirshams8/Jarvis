package com.jarvis.assistant.media

import android.content.Context
import android.content.Intent
import com.jarvis.assistant.core.Constants
import com.jarvis.assistant.core.JarvisCore
import com.jarvis.assistant.utils.Logger

class MusicPlayerManager(private val context: Context) {
    
    private var currentPlayer: String = "spotify"
    
    fun playSong(query: String) {
        when (currentPlayer) {
            "spotify" -> playOnSpotify(query)
            "youtube" -> playOnYouTube(query)
            "jiosaavn" -> playOnJioSaavn(query)
            else -> {
                JarvisCore.speak("Unknown music player")
            }
        }
    }
    
    private fun playOnSpotify(query: String) {
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setPackage(Constants.SPOTIFY_PKG)
                data = android.net.Uri.parse("spotify:search:$query")
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            JarvisCore.speak("Playing $query on Spotify")
        } catch (e: Exception) {
            JarvisCore.speak("Failed to play on Spotify")
            Logger.log("Spotify error: ${e.message}", Logger.Level.ERROR)
        }
    }
    
    private fun playOnYouTube(query: String) {
        try {
            val intent = Intent(Intent.ACTION_SEARCH).apply {
                setPackage(Constants.YT_MUSIC_PKG)
                putExtra("query", query)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
            JarvisCore.speak("Searching $query on YouTube Music")
        } catch (e: Exception) {
            JarvisCore.speak("Failed to play on YouTube Music")
            Logger.log("YouTube Music error: ${e.message}", Logger.Level.ERROR)
        }
    }
    
    private fun playOnJioSaavn(query: String) {
        try {
            val intent = context.packageManager.getLaunchIntentForPackage(Constants.JIOSAAVN_PKG)
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                JarvisCore.speak("Opening JioSaavn")
            } else {
                JarvisCore.speak("JioSaavn not installed")
            }
        } catch (e: Exception) {
            Logger.log("JioSaavn error: ${e.message}", Logger.Level.ERROR)
        }
    }
    
    fun switchPlayer(playerName: String) {
        currentPlayer = playerName.lowercase()
        JarvisCore.speak("Switched to $playerName")
    }
}
