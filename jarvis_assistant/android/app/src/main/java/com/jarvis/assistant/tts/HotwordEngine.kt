package com.jarvis.assistant.tts

import android.content.Context
import com.jarvis.assistant.core.JarvisCore
import com.jarvis.assistant.utils.Logger
import kotlinx.coroutines.*

class HotwordEngine(private val context: Context) {
    private var isRunning = false
    private val scope = CoroutineScope(Dispatchers.Default)

    fun start() {
        isRunning = true
        Logger.log("Hotword engine started", Logger.Level.INFO)
        scope.launch {
            while (isRunning) {
                delay(100)
                // TODO: Whisper model integration point
            }
        }
    }

    fun stop() {
        isRunning = false
        Logger.log("Hotword engine stopped", Logger.Level.INFO)
    }

    fun isHealthy() = isRunning
}
