package com.jarvis.assistant.core

object Constants {
    // API Keys
    const val OPENAI_API_KEY = "your-openai-key"
    const val ELEVENLABS_API_KEY = "your-elevenlabs-key"
    const val ELEVENLABS_VOICE_ID = "your-voice-id"
    const val DEEPSEEK_API_KEY = "your-deepseek-key"
    const val GEMINI_API_KEY = "your-gemini-key"
    
    // API Endpoints
    const val OPENAI_ENDPOINT = "https://api.openai.com/v1/chat/completions"
    const val DEEPSEEK_ENDPOINT = "https://api.deepseek.com/v1/chat/completions"
    const val GEMINI_ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent"
    const val ELEVENLABS_ENDPOINT = "https://api.elevenlabs.io/v1/text-to-speech"
    
    // App Packages
    const val PHYSICS_WALLAH_PKG = "com.physicswallah.app"
    const val WAVELET_PKG = "com.pittvandewitt.wavelet"
    const val STOPWATCH_PKG = "com.google.android.deskclock"
    const val SPOTIFY_PKG = "com.spotify.music"
    const val YT_MUSIC_PKG = "com.google.android.apps.youtube.music"
    const val JIOSAAVN_PKG = "com.jio.media.jiobeats"
    const val WHATSAPP_PKG = "com.whatsapp"
    
    // Hotwords
    val HOTWORDS = listOf("jarvis", "hey jarvis", "ok jarvis")
    
    // Lockdown
    val LOCKDOWN_WHITELIST = listOf("+1234567890")
}
