package com.jarvis.assistant.device

import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.bluetooth.BluetoothAdapter
import android.hardware.camera2.CameraManager
import android.os.PowerManager
import android.provider.Settings

class DeviceController(private val context: Context) {
    
    private val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    private var torchEnabled = false
    
    fun toggleWifi() {
        // Note: Direct WiFi control deprecated in Android 10+
        // Opens WiFi settings instead
        val intent = Intent(Settings.ACTION_WIFI_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
        JarvisCore.speak("Opening WiFi settings")
    }
    
    fun toggleBluetooth() {
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        
        if (bluetoothAdapter?.isEnabled == true) {
            bluetoothAdapter.disable()
            JarvisCore.speak("Bluetooth disabled")
        } else {
            bluetoothAdapter?.enable()
            JarvisCore.speak("Bluetooth enabled")
        }
    }
    
    fun toggleTorch() {
        try {
            val cameraId = cameraManager.cameraIdList[0]
            torchEnabled = !torchEnabled
            cameraManager.setTorchMode(cameraId, torchEnabled)
            
            JarvisCore.speak(if (torchEnabled) "Torch on" else "Torch off")
        } catch (e: Exception) {
            Logger.log("Torch control failed: ${e.message}")
        }
    }
    
    fun toggleRotation() {
        val currentRotation = Settings.System.getInt(
            context.contentResolver,
            Settings.System.ACCELEROMETER_ROTATION,
            0
        )
        
        val intent = Intent(Settings.ACTION_DISPLAY_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
        
        JarvisCore.speak("Opening rotation settings")
    }
    
    fun enablePowerSaving() {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        
        if (powerManager.isPowerSaveMode) {
            JarvisCore.speak("Power saving already enabled")
        } else {
            val intent = Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            JarvisCore.speak("Opening power saving settings")
        }
    }
    
    fun sleepMode() {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(
            PowerManager.SCREEN_DIM_WAKE_LOCK,
            "JARVIS::SleepMode"
        )
        
        JarvisCore.speak("Good night. Entering sleep mode.")
        
        // Dim screen
        // Note: Actual screen off requires system permissions
    }
}