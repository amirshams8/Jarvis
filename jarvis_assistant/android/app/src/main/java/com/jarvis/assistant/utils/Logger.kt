package com.jarvis.assistant.utils

import android.util.Log

object Logger {
    enum class Level { DEBUG, INFO, WARNING, ERROR }

    private val logs = mutableListOf<LogEntry>()

    data class LogEntry(val timestamp: Long, val level: Level, val message: String)

    fun log(message: String, level: Level = Level.INFO) {
        val entry = LogEntry(System.currentTimeMillis(), level, message)
        logs.add(entry)
        if (logs.size > 1000) logs.removeAt(0)

        when (level) {
            Level.DEBUG -> Log.d("JARVIS", message)
            Level.INFO -> Log.i("JARVIS", message)
            Level.WARNING -> Log.w("JARVIS", message)
            Level.ERROR -> Log.e("JARVIS", message)
        }
    }

    fun getRecentLogs(count: Int = 100): List<LogEntry> = logs.takeLast(count)

    fun clearLogs() = logs.clear()
}
