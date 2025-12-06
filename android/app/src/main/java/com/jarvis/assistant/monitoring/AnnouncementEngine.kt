package com.jarvis.assistant.monitoring

import android.content.Context
import com.jarvis.assistant.core.JarvisCore
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

class AnnouncementEngine(private val context: Context) {
    
    private val scope = CoroutineScope(Dispatchers.Default)
    
    fun scheduleAnnouncement(message: String, delayMillis: Long) {
        scope.launch {
            delay(delayMillis)
            JarvisCore.announce(message)
        }
    }
    
    fun announceTime() {
        val time = SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date())
        JarvisCore.announce("The time is $time")
    }
    
    fun announceDate() {
        val date = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date())
        JarvisCore.announce("Today is $date")
    }
    
    fun announceWeather(temperature: Int, condition: String) {
        JarvisCore.announce("Current temperature is $temperature degrees and $condition")
    }
    
    fun announceReminder(reminderText: String) {
        JarvisCore.announce("Reminder: $reminderText")
    }
    
    fun announceNotification(appName: String, title: String) {
        JarvisCore.announce("New notification from $appName: $title")
    }
    
    fun shutdown() {
        scope.cancel()
    }
}
