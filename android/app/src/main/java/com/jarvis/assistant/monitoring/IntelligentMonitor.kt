package com.jarvis.assistant.monitoring

import android.content.Context
import android.os.BatteryManager
import com.jarvis.assistant.core.JarvisCore
import com.jarvis.assistant.utils.Logger
import com.jarvis.assistant.utils.NetworkUtils
import kotlinx.coroutines.*

class IntelligentMonitor(private val context: Context) {
    
    private val scope = CoroutineScope(Dispatchers.Default)
    private var isMonitoring = false
    
    private val notificationPatterns = mutableMapOf<String, MutableList<Long>>()
    private val appUsageStats = mutableMapOf<String, Long>()
    
    fun startMonitoring() {
        if (isMonitoring) return
        isMonitoring = true
        
        scope.launch {
            monitorLoop()
        }
        
        Logger.log("Intelligent monitoring started", Logger.Level.INFO)
    }
    
    private suspend fun monitorLoop() {
        while (isMonitoring) {
            try {
                checkBattery()
                checkNetwork()
                analyzeNotificationPatterns()
                delay(30000)
            } catch (e: Exception) {
                Logger.log("Monitor error: ${e.message}", Logger.Level.ERROR)
                delay(60000)
            }
        }
    }
    
    private fun checkBattery() {
        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val batteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        
        if (batteryLevel <= 20) {
            Logger.log("Low battery: $batteryLevel%", Logger.Level.WARNING)
            JarvisCore.announce("Battery level is low at $batteryLevel percent")
        }
    }
    
    private fun checkNetwork() {
        val isConnected = NetworkUtils.isConnected(context)
        if (!isConnected) {
            Logger.log("Network disconnected", Logger.Level.WARNING)
        }
    }
    
    private fun analyzeNotificationPatterns() {
        val patterns = mutableListOf<String>()
        
        for ((packageName, timestamps) in notificationPatterns) {
            if (timestamps.size >= 5) {
                val timeWindow = System.currentTimeMillis() - 300000
                val recentNotifs = timestamps.filter { it > timeWindow }
                
                if (recentNotifs.size >= 5) {
                    patterns.add("High notification frequency from $packageName")
                    JarvisCore.announce("You have many notifications from $packageName")
                }
            }
        }
        
        if (patterns.isNotEmpty()) {
            Logger.log("Detected patterns: $patterns", Logger.Level.INFO)
        }
    }
    
    fun recordNotification(packageName: String) {
        if (!notificationPatterns.containsKey(packageName)) {
            notificationPatterns[packageName] = mutableListOf()
        }
        notificationPatterns[packageName]?.add(System.currentTimeMillis())
    }
    
    fun generateInsights(): String {
        val insights = StringBuilder()
        insights.append("System Insights:\n")
        insights.append("- Monitored apps: ${notificationPatterns.size}\n")
        insights.append("- Active monitoring: $isMonitoring\n")
        return insights.toString()
    }
    
    fun stopMonitoring() {
        isMonitoring = false
        scope.cancel()
        Logger.log("Intelligent monitoring stopped", Logger.Level.INFO)
    }
}
