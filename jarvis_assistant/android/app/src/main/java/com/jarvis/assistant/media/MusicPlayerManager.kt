package com.jarvis.assistant.media

import android.content.Context
import android.content.Intent
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState

class MusicPlayerManager(private val context: Context) {
    
    private val mediaSessionManager = context.getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
    
    fun getCurrentPlayer(): String? {
        val controllers = getActiveMediaControllers()
        return controllers.firstOrNull()?.packageName
    }
    
    fun getCurrentTrack(): TrackInfo? {
        val controller = getActiveMediaControllers().firstOrNull() ?: return null
        val metadata = controller.metadata ?: return null
        
        return TrackInfo(
            title = metadata.description.title?.toString() ?: "Unknown",
            artist = metadata.description.subtitle?.toString() ?: "Unknown",
            album = metadata.description.description?.toString() ?: "",
            duration = metadata.getLong("android.media.metadata.DURATION")
        )
    }
    
    fun getPlaybackState(): PlaybackState? {
        return getActiveMediaControllers().firstOrNull()?.playbackState
    }
    
    fun isPlaying(): Boolean {
        val state = getPlaybackState()
        return state?.state == PlaybackState.STATE_PLAYING
    }
    
    fun playSpotifyPlaylist(playlistUri: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = android.net.Uri.parse(playlistUri)
            setPackage(Constants.SPOTIFY_PKG)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }
    
    fun searchAndPlay(query: String, app: String = "spotify") {
        when (app.lowercase()) {
            "spotify" -> {
                JarvisCore.service.mediaController.playSpotify(query)
            }
            "youtube", "youtube music" -> {
                JarvisCore.service.mediaController.playYouTubeMusic(query)
            }
            "jiosaavn" -> {
                JarvisCore.service.mediaController.playJioSaavn(query)
            }
        }
    }
    
    private fun getActiveMediaControllers(): List<MediaController> {
        return try {
            mediaSessionManager.getActiveSessions(null)
        } catch (e: Exception) {
            Logger.log("Failed to get media controllers: ${e.message}")
            emptyList()
        }
    }
    
    data class TrackInfo(
        val title: String,
        val artist: String,
        val album: String,
        val duration: Long
    )
}