package com.jarvis.assistant.learning

import android.content.Context
import com.jarvis.assistant.core.JarvisCore

class AnnouncementEngine(private val context: Context) {
    fun announce(message: String) {
        JarvisCore.speak(message)
    }

    fun announceNotification(pkg: String, title: String, text: String) {
        // Smart filtering
    }
}
