package com.jarvis.assistant.monitoring

import android.content.Context
import kotlinx.coroutines.*

class AnnouncementEngine(private val context: Context) {
    
    private val scope = CoroutineScope(Dispatchers.Main)
    private val alerts = mutableListOf<Alert>()
    
    data class Alert(
        val category: String,
        val message: String,
        val timestamp: Long,
        val priority: Priority
    )
    
    enum class Priority {
        LOW, MEDIUM, HIGH, CRITICAL
    }
    
    fun announce(message: String, priority: Priority = Priority.MEDIUM) {
        scope.launch {
            when (priority) {
                Priority.CRITICAL -> {
                    JarvisCore.speak("Alert! $message")
                }
                Priority.HIGH -> {
                    JarvisCore.speak("Important: $message")
                }
                Priority.MEDIUM -> {
                    JarvisCore.speak(message)
                }
                Priority.LOW -> {
                    // Queue for later
                }
            }
        }
    }
    
    fun logAlert(category: String, message: String, priority: Priority = Priority.MEDIUM) {
        alerts.add(Alert(category, message, System.currentTimeMillis(), priority))
        
        // Auto-announce based on priority
        if (priority >= Priority.HIGH) {
            announce(message, priority)
        }
    }
    
    fun announceNotification(app: String, title: String, text: String) {
        // Intelligently announce important notifications
        val important = isImportantNotification(app, title, text)
        
        if (important) {
            JarvisCore.speak("Notification from $app: $title")
        }
    }
    
    private fun isImportantNotification(app: String, title: String, text: String): Boolean {
        // Check if notification contains important keywords
        val keywords = listOf("urgent", "important", "alert", "critical", "emergency")
        return keywords.any { text.contains(it, ignoreCase = true) || title.contains(it, ignoreCase = true) }
    }
    
    fun announceBatteryEvent(level: Int, isCharging: Boolean) {
        when {
            level < 10 && !isCharging -> {
                announce("Critical battery warning: $level percent remaining", Priority.CRITICAL)
            }
            level < 20 && !isCharging -> {
                announce("Battery low: $level percent", Priority.HIGH)
            }
            level == 100 && isCharging -> {
                announce("Battery fully charged", Priority.LOW)
            }
        }
    }
    
    fun announceNetworkChange(connected: Boolean) {
        if (connected) {
            announce("Internet connection restored", Priority.MEDIUM)
        } else {
            announce("Internet connection lost. Switching to offline mode.", Priority.HIGH)
        }
    }
}