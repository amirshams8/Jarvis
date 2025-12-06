package com.jarvis.assistant.modes

class PersonalityManager {
    enum class PersonalityMode {
        JARVIS, SILENT_GUARDIAN, COMBAT, HUMOR, STUDY_FOCUS, CALM_WHISPER
    }

    private var currentMode = PersonalityMode.JARVIS

    data class ResponseStyle(
        val prefix: String = "",
        val whisper: Boolean = false,
        val urgent: Boolean = false
    )

    fun switchMode(mode: PersonalityMode) {
        currentMode = mode
    }

    fun getCurrentMode(): PersonalityMode = currentMode

    fun getResponseStyle(): ResponseStyle = when (currentMode) {
        PersonalityMode.JARVIS -> ResponseStyle("Sir, ")
        PersonalityMode.SILENT_GUARDIAN -> ResponseStyle("", whisper = true)
        PersonalityMode.COMBAT -> ResponseStyle("Alert: ", urgent = true)
        PersonalityMode.HUMOR -> ResponseStyle("Well, ")
        PersonalityMode.STUDY_FOCUS -> ResponseStyle("", whisper = true)
        PersonalityMode.CALM_WHISPER -> ResponseStyle("", whisper = true)
    }
}
