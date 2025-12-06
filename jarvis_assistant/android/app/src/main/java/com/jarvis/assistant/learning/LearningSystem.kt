package com.jarvis.assistant.learning

import android.content.Context
import com.jarvis.assistant.utils.MemoryManager

class LearningSystem(private val context: Context) {
    private val memory = MemoryManager(context)
    private val commandFrequency = mutableMapOf<String, Int>()

    fun recordCommand(command: String) {
        commandFrequency[command] = (commandFrequency[command] ?: 0) + 1
    }

    fun getFrequentCommands(): Map<String, Int> = commandFrequency.toList()
        .sortedByDescending { it.second }
        .take(10)
        .toMap()

    fun isHealthy() = true
}
