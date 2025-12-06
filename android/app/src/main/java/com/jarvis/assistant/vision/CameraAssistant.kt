package com.jarvis.assistant.vision

import android.content.Context
import com.jarvis.assistant.utils.Logger

class CameraAssistant(private val context: Context) {
    private var isMonitoring = false

    fun startMonitoring() {
        isMonitoring = true
        Logger.log("Camera monitoring started", Logger.Level.INFO)
    }

    fun stopMonitoring() {
        isMonitoring = false
        Logger.log("Camera monitoring stopped", Logger.Level.INFO)
    }
}
