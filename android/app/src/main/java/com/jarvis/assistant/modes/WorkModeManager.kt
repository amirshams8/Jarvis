package com.jarvis.assistant.modes

import android.content.Context
import android.content.Intent
import com.jarvis.assistant.utils.Constants
import com.jarvis.assistant.utils.Logger

class WorkModeManager(private val context: Context) {
    private var isActive = false

    fun activate() {
        isActive = true
        Logger.log("✅ Work mode activated", Logger.Level.INFO)

        launchApp(Constants.PHYSICS_WALLAH)
        launchApp(Constants.STOPWATCH)
        launchApp(Constants.WAVELET)
        launchApp(Constants.YOUTUBE_MUSIC)
    }

    fun deactivate() {
        isActive = false
        Logger.log("Work mode deactivated", Logger.Level.INFO)
    }

    private fun launchApp(packageName: String) {
        try {
            val intent = context.packageManager.getLaunchIntentForPackage(packageName)
            intent?.let {
                it.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(it)
            }
        } catch (e: Exception) {
            Logger.log("Failed to launch $packageName: ${e.message}", Logger.Level.ERROR)
        }
    }

    fun isHealthy(): Boolean = true
}
