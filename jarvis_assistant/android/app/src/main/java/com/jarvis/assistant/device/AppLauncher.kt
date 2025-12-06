package com.jarvis.assistant.device

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager

class AppLauncher(private val context: Context) {
    
    private val packageManager = context.packageManager
    
    fun launchApp(appName: String) {
        val packageName = findPackageByName(appName)
        
        if (packageName != null) {
            val intent = packageManager.getLaunchIntentForPackage(packageName)
            intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            
            try {
                context.startActivity(intent)
                JarvisCore.speak("Opening $appName")
            } catch (e: Exception) {
                JarvisCore.speak("Failed to open $appName")
            }
        } else {
            JarvisCore.speak("$appName not found")
        }
    }
    
    private fun findPackageByName(appName: String): String? {
        val installedApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        
        val normalizedSearch = appName.lowercase().replace(" ", "")
        
        return installedApps.firstOrNull { appInfo ->
            val appLabel = packageManager.getApplicationLabel(appInfo).toString().lowercase().replace(" ", "")
            appLabel.contains(normalizedSearch) || normalizedSearch.contains(appLabel)
        }?.packageName
    }
    
    fun getInstalledApps(): List<String> {
        val installedApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
        return installedApps.map { packageManager.getApplicationLabel(it).toString() }
    }
}