package com.jarvis.assistant.engines

import android.content.Context
import android.media.AudioRecord
import com.jarvis.assistant.core.Constants
import com.jarvis.assistant.core.JarvisCore
import com.jarvis.assistant.utils.Logger
import kotlinx.coroutines.*

class HotwordEngine(private val context: Context) {
    private var isListening = false
    private var audioRecord: AudioRecord? = null
    private val scope = CoroutineScope(Dispatchers.IO)
    private var lastDetectionTime = 0L

    fun start() {
        if (isListening) return
        isListening = true
        scope.launch {
            startListeningLoop()
        }
        Logger.log("Hotword Engine Started")
    }

    private suspend fun startListeningLoop() {
        while (isListening) {
            try {
                delay(100)
            } catch (e: Exception) {
                Logger.log("Hotword error: ${e.message}")
                delay(1000)
            }
        }
    }

    private fun onHotwordDetected() {
        Logger.log("Hotword detected!")
        JarvisCore.processCommand("test command")
    }

    fun stop() {
        isListening = false
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
    }

    fun isHealthy(): Boolean = isListening

    fun restart() {
        scope.launch {
            stop()
            delay(500)
            start()
        }
    }
}
