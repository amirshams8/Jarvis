package com.jarvis.assistant.utils

import android.content.Context
import android.content.SharedPreferences

class MemoryManager(private val context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("jarvis_memory", Context.MODE_PRIVATE)

    fun store(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    fun retrieve(key: String): String? = prefs.getString(key, null)
}
