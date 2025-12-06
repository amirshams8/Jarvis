package com.jarvis.assistant.device

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import com.jarvis.assistant.core.JarvisCore

class AppLauncher(private val context: Context) {
    
    fun launchApp(packageName: String) {
        try {
            val intent = context.packageManager.getLaunchIntentForPackage(packageName)
            if (intent != null) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
                JarvisCore.speak("Opening app")
            } else {
                JarvisCore.speak("App not found")
            }
        } catch (e: Exception) {
            JarvisCore.speak("Failed to open app")
        }
    }
    
    fun isAppInstalled(packageName: String): Boolean {
        return try {
            context.packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
    
    fun getInstalledApps(): List<String> {
        val apps = mutableListOf<String>()
        val packages = context.packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        for (packageInfo in packages) {
            apps.add(packageInfo.packageName)
        }
        return apps
    }
}
