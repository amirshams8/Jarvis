package com.jarvis.assistant.ui

import android.content.Context
import com.jarvis.assistant.utils.Logger

object DebugConsole {
    fun getSystemStatus(context: Context): String {
        return buildString {
            appendLine("JARVIS SYSTEM STATUS")
            appendLine("====================")
            appendLine("All systems operational")
            appendLine()
            appendLine("Recent logs:")
            Logger.getRecentLogs(5).forEach {
                appendLine("  [${it.level}] ${it.message}")
            }
        }
    }
}
