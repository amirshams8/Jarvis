package com.jarvis.assistant.automation

import android.content.Context
import com.jarvis.assistant.utils.Logger

class AutomationEngine(private val context: Context) {
    
    fun restart() {
        Logger.log("AutomationEngine restarting", Logger.Level.INFO)
    }
    
    fun start() {
        Logger.log("AutomationEngine started", Logger.Level.INFO)
    }
    
    fun stop() {
        Logger.log("AutomationEngine stopped", Logger.Level.INFO)
    }
}
