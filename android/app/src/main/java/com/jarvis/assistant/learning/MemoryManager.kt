package com.jarvis.assistant.learning

import android.content.Context
import com.jarvis.assistant.utils.Logger

class MemoryManager(private val context: Context) {
    private val prefs = context.getSharedPreferences("jarvis_memory", Context.MODE_PRIVATE)

    fun storePreference(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
        Logger.log("Stored preference: $key", Logger.Level.INFO)
    }

    fun getPreference(key: String): String? {
        return prefs.getString(key, null)
    }

    fun clearMemory() {
        prefs.edit().clear().apply()
        Logger.log("Memory cleared", Logger.Level.INFO)
    }
}
