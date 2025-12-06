package com.jarvis.assistant.vision

import android.content.Context
import com.jarvis.assistant.utils.Logger

class ScreenAssistant(private val context: Context) {
    private var isMonitoring = false

    fun startMonitoring() {
        isMonitoring = true
        Logger.log("Screen monitoring started", Logger.Level.INFO)
    }

    fun stopMonitoring() {
        isMonitoring = false
    }
}
