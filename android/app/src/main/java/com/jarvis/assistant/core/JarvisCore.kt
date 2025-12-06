package com.jarvis.assistant.core

import android.content.Context
import com.jarvis.assistant.utils.Logger
import kotlinx.coroutines.*

object JarvisCore {
    lateinit var context: Context
    lateinit var service: JarvisService

    fun initialize(context: Context) {
        this.context = context
    }

    fun processCommand(command: String) {
        val lower = command.lowercase()
        Logger.log("CMD: $command", Logger.Level.INFO)

        when {
            "work mode" in lower || "grind" in lower -> speak("Work mode activated")
            "lockdown" in lower -> speak("Lockdown activated")
            "play" in lower -> speak("Playing")
            else -> handleWithLLM(command)
        }
    }

    private fun handleWithLLM(command: String) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                speak("Processing: $command")
            } catch (e: Exception) {
                speak("Error processing request")
                Logger.log("LLM error: ${e.message}", Logger.Level.ERROR)
            }
        }
    }

    fun speak(text: String) {
        Logger.log("SPEAK: $text", Logger.Level.INFO)
    }
    
    fun announce(text: String) = speak(text)
    
    fun processNotification(packageName: String, title: String, text: String) {
        Logger.log("Notification from $packageName: $title")
    }
}
