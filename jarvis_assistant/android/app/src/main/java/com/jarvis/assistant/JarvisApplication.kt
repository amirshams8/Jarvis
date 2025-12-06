package com.jarvis.assistant

import android.app.Application
import android.content.Intent
import com.jarvis.assistant.core.JarvisService
import com.jarvis.assistant.utils.Logger

class JarvisApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Logger.log("🚀 JARVIS Application starting...", Logger.Level.INFO)

        // Start foreground service
        val intent = Intent(this, JarvisService::class.java)
        startForegroundService(intent)
    }
}
