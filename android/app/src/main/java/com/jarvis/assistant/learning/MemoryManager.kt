package com.jarvis.assistant.learning

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONObject

class MemoryManager(private val context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences("jarvis_memory", Context.MODE_PRIVATE)
    
    fun storePreference(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
        Logger.log("Stored preference: $key")
    }
    
    fun getPreference(key: String): String? {
        return prefs.getString(key, null)
    }
    
    fun storeVoicePattern(userId: String, pattern: FloatArray) {
        // Store voice biometric data
        // Implementation for voice recognition
    }
    
    fun storeDailyHabit(habit: String) {
        val habits = getHabits().toMutableList()
        if (!habits.contains(habit)) {
            habits.add(habit)
            prefs.edit().putString("habits", habits.joinToString(",")).apply()
        }
    }
    
    fun getHabits(): List<String> {
        val habitsString = prefs.getString("habits", "") ?: ""
        return if (habitsString.isEmpty()) emptyList() else habitsString.split(",")
    }
    
    fun clearMemory() {
        prefs.edit().clear().apply()
        Logger.log("Memory cleared")
    }
    
    fun exportMemory(): String {
        val allPrefs = prefs.all
        val json = JSONObject(allPrefs as Map<*, *>)
        return json.toString(2)
    }
}