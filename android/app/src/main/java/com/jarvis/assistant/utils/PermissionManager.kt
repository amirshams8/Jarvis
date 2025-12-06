package com.jarvis.assistant.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Settings
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class PermissionManager(private val context: Context) {

    data class PermissionStatus(
        val allGranted: Boolean,
        val deniedPermissions: List<String>,
        val accessibilityEnabled: Boolean,
        val notificationEnabled: Boolean,
        val overlayEnabled: Boolean
    )

    fun checkAllPermissions(): PermissionStatus {
        val required = listOf(
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.CALL_PHONE
        )

        val denied = required.filter {
            ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
        }

        return PermissionStatus(
            allGranted = denied.isEmpty(),
            deniedPermissions = denied,
            accessibilityEnabled = false, // TODO: Check accessibility
            notificationEnabled = false,  // TODO: Check notification access
            overlayEnabled = Settings.canDrawOverlays(context)
        )
    }

    fun requestPermissions(activity: Activity) {
        val permissions = arrayOf(
            android.Manifest.permission.RECORD_AUDIO,
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.CALL_PHONE
        )
        ActivityCompat.requestPermissions(activity, permissions, 100)
    }

    fun openAccessibilitySettings() {
        val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun openNotificationSettings() {
        val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun getMissingPermissionsMessage(): String {
        val status = checkAllPermissions()
        return when {
            !status.allGranted -> "Missing permissions: ${status.deniedPermissions.size}"
            !status.accessibilityEnabled -> "Enable accessibility service"
            else -> "All permissions granted"
        }
    }
}
