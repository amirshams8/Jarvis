package com.jarvis.assistant.device

import android.content.Context
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.provider.Settings
import com.jarvis.assistant.core.JarvisCore
import com.jarvis.assistant.utils.Logger

class DeviceController(private val context: Context) {
    
    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private var isFlashlightOn = false
    
    fun toggleFlashlight() {
        try {
            val cameraId = cameraManager.cameraIdList[0]
            isFlashlightOn = !isFlashlightOn
            cameraManager.setTorchMode(cameraId, isFlashlightOn)
            
            JarvisCore.speak(if (isFlashlightOn) "Flashlight on" else "Flashlight off")
            Logger.log("Flashlight toggled: $isFlashlightOn", Logger.Level.INFO)
        } catch (e: Exception) {
            JarvisCore.speak("Failed to toggle flashlight")
            Logger.log("Flashlight error: ${e.message}", Logger.Level.ERROR)
        }
    }
    
    fun adjustVolume(direction: String) {
        when (direction.lowercase()) {
            "up", "increase", "louder" -> {
                audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI)
                JarvisCore.speak("Volume increased")
            }
            "down", "decrease", "lower" -> {
                audioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI)
                JarvisCore.speak("Volume decreased")
            }
            "max", "maximum" -> {
                audioManager.setStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                    AudioManager.FLAG_SHOW_UI
                )
                JarvisCore.speak("Volume set to maximum")
            }
            "mute" -> {
                audioManager.adjustVolume(AudioManager.ADJUST_MUTE, AudioManager.FLAG_SHOW_UI)
                JarvisCore.speak("Volume muted")
            }
        }
    }
    
    fun getBrightnessLevel(): Int {
        return try {
            Settings.System.getInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS)
        } catch (e: Exception) {
            Logger.log("Failed to get brightness: ${e.message}", Logger.Level.ERROR)
            0
        }
    }
    
    fun setBrightness(level: Int) {
        try {
            val brightness = level.coerceIn(0, 255)
            Settings.System.putInt(context.contentResolver, Settings.System.SCREEN_BRIGHTNESS, brightness)
            JarvisCore.speak("Brightness adjusted")
        } catch (e: Exception) {
            Logger.log("Failed to set brightness: ${e.message}", Logger.Level.ERROR)
            JarvisCore.speak("Unable to adjust brightness")
        }
    }
}
