package com.jarvis.assistant.utils

object Constants {
    // ✅ PUTER.JS - FREE GPT-5/DeepSeek/Gemini (NO API KEYS!)
    const val PUTER_AI_URL = "https://api.puter.com/v2/ai/chat"

    val ALL_LLMS = listOf(
        "gpt-5.1", "gpt-5-nano", "gpt-5-mini", "gpt-4.1-mini", "gpt-5-chat-latest",
        "o1-mini", "o1-pro",
        "deepseek/deepseek-r1", "deepseek/deepseek-chat", "deepseek/deepseek-v3.1",
        "gemini-3-pro-preview", "gemini-2.5-pro", "gemini-2.5-flash", 
        "gemini-2.0-flash", "gemini-1.5-flash"
    )

    const val DEFAULT_LLM = "gpt-5-nano"
    const val REASONING_LLM = "deepseek/deepseek-r1"
    const val QUALITY_LLM = "gemini-3-pro-preview"

    // 🔥 ELEVENLABS VOICE MODEL - PASTE YOUR API KEY HERE 👇
    const val ELEVENLABS_API_KEY = "26b7fa17210a19b9855a8cccc2d79ea2db3314a7b9a19e85e568a4d8eb7c40cd"        // ← PASTE YOUR KEY
    const val ELEVENLABS_VOICE_ID = "wCTIlMpUbSWWk9qNkIPk"  // JARVIS voice
    const val ELEVENLABS_MODEL_ID = "eleven_monolingual_v2"
    const val USE_ELEVENLABS = true

    // Voice settings
    const val VOICE_STABILITY = 0.5f
    const val VOICE_SIMILARITY = 0.8f
    const val VOICE_STYLE = 0.0f
    
    
    // Porcupine Hotword
    const val PORCUPINE_ACCESS_KEY = "Z/ixK/Y8Uz8eky9TtaVDPHtJuyWQZCwnsq4kkv1VKEY6gvYpPi+SWA=="  // ← Paste here
    const val HOTWORD_SENSITIVITY = 0.5f


    // Work mode apps
    const val PHYSICS_WALLAH = "xyz.penpencil.physicswala"
    const val WAVELET = "com.pittvandewitt.wavelet"
    const val STOPWATCH = "com.sec.android.app.clockpackage"
    const val YOUTUBE_MUSIC = "com.google.android.apps.youtube.music"

    // Lockdown whitelist
    val LOCKDOWN_WHITELIST = listOf(
        "Mom" to "+9198010 46534",
        "Dad" to "+919931298765",
        "Emergency" to "112"
    )

    const val TTS_SPEAKING_RATE = 1.0f
    const val TTS_PITCH = 1.0f
}
