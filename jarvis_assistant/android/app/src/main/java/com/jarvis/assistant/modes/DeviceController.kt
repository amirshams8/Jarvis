package com.jarvis.assistant.modes

import android.content.Context
import android.content.Intent
import android.hardware.camera2.CameraManager
import android.net.wifi.WifiManager
import android.provider.Settings
import com.jarvis.assistant.utils.Logger

class DeviceController(private val context: Context) {
    private val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    private var torchOn = false

    fun toggleWifi() {
        context.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    fun toggleBluetooth() {
        context.startActivity(Intent(Settings.ACTION_BLUETOOTH_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    fun toggleTorch() {
        try {
            val cameraId = cameraManager.cameraIdList[0]
            cameraManager.setTorchMode(cameraId, !torchOn)
            torchOn = !torchOn
            Logger.log("Torch ${if (torchOn) "ON" else "OFF"}", Logger.Level.INFO)
        } catch (e: Exception) {
            Logger.log("Torch error: ${e.message}", Logger.Level.ERROR)
        }
    }
}
