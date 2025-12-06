package com.jarvis.assistant.core

import com.jarvis.assistant.utils.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object JarvisCore {
    lateinit var service: JarvisService

    fun initialize(jarvisService: JarvisService) {
        service = jarvisService
    }

    fun processCommand(command: String) {
        val lower = command.lowercase()
        Logger.log("CMD: $command", Logger.Level.INFO)

        when {
            "gpt" in lower || "openai" in lower -> service.llmManager.switchLLM("gpt-5-nano")
            "deepseek" in lower -> service.llmManager.switchLLM("deepseek/deepseek-r1")
            "gemini" in lower -> service.llmManager.switchLLM("gemini-3-pro-preview")
            "switch to" in lower -> {
                val model = command.substringAfter("switch to", "").trim()
                service.llmManager.switchLLM(model)
            }
            "play" in lower, "music" in lower -> service.mediaController.playPause()
            "next" in lower -> service.mediaController.next()
            "previous" in lower -> service.mediaController.previous()
            "work mode" in lower, "grind" in lower -> service.workModeManager.activate()
            "lockdown" in lower -> service.lockdownManager.enableLockdown()
            else -> handleWithLLM(command)
        }
    }

    private fun handleWithLLM(command: String) {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val response = service.llmManager.query(command)
                service.speak(response)
            } catch (e: Exception) {
                service.speak("Error processing request")
                Logger.log("LLM error: ${e.message}", Logger.Level.ERROR)
            }
        }
    }

    fun speak(text: String) = service.speak(text)
}
