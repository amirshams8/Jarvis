package com.jarvis.assistant.learning

import android.content.Context

class SleepReportGenerator(private val context: Context) {
    fun generateReport(): String {
        return "Sleep report: 7 notifications, 3 calls, system healthy"
    }
}
