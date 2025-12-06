package com.jarvis.assistant.utils

import android.util.Log
import java.text.SimpleDateFormat
import java.util.*

object Logger {
    enum class Level { INFO, ERROR, WARNING, DEBUG }
    
    private val logs = mutableListOf<LogEntry>()
    private const val MAX_LOGS = 500
    
    data class LogEntry(val level: Level, val message: String, val timestamp: String)
    
    fun log(message: String, level: Level = Level.INFO) {
        val timestamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        val entry = LogEntry(level, message, timestamp)
        
        synchronized(logs) {
            logs.add(entry)
            if (logs.size > MAX_LOGS) logs.removeAt(0)
        }
        
        when (level) {
            Level.INFO -> Log.i("JARVIS", message)
            Level.ERROR -> Log.e("JARVIS", message)
            Level.WARNING -> Log.w("JARVIS", message)
            Level.DEBUG -> Log.d("JARVIS", message)
        }
    }
    
    fun getRecentLogs(count: Int = 50): List<LogEntry> {
        return synchronized(logs) {
            logs.takeLast(count)
        }
    }
}
