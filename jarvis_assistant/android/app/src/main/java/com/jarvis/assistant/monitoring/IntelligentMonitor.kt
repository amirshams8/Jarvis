package com.jarvis.assistant.monitoring

import android.content.Context
import android.os.BatteryManager
import android.telephony.TelephonyManager
import kotlinx.coroutines.*
import java.util.*

class IntelligentMonitor(private val context: Context) {
    
    private var isMonitoring = false
    private val scope = CoroutineScope(Dispatchers.Default)
    private val events = mutableListOf<MonitoringEvent>()
    private var lastUserActivityTime = System.currentTimeMillis()
    
    data class MonitoringEvent(
        val type: String,
        val message: String,
        val timestamp: Long,
        val data: Map<String, Any> = emptyMap()
    )
    
    fun startMonitoring() {
        if (isMonitoring) return
        isMonitoring = true
        
        scope.launch {
            monitoringLoop()
        }
        
        Logger.log("24/7 Intelligent Monitoring Started")
    }
    
    fun stopMonitoring() {
        isMonitoring = false
    }
    
    private suspend fun monitoringLoop() {
        while (isMonitoring) {
            try {
                // Check device stats
                monitorBattery()
                monitorNetwork()
                monitorServices()
                
                // Check if user is asleep (no activity for 4+ hours)
                if (isUserAsleep()) {
                    generateAndDeliverSleepReport()
                }
                
                delay(60000) // Check every minute
            } catch (e: Exception) {
                Logger.log("Monitoring error: ${e.message}")
            }
        }
    }
    
    private fun monitorBattery() {
        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val level = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        val isCharging = batteryManager.isCharging
        
        // Alert on low battery
        if (level < 20 && !isCharging) {
            val event = MonitoringEvent(
                type = "battery",
                message = "Battery low: $level%",
                timestamp = System.currentTimeMillis(),
                data = mapOf("level" to level, "charging" to isCharging)
            )
            events.add(event)
            
            if (level < 15) {
                JarvisCore.announce("Battery critically low at $level percent")
            }
        }
    }
    
    private fun monitorNetwork() {
        val isConnected = NetworkUtils.isConnected(context)
        
        // Store network status
        // Alert on connection changes
    }
    
    private fun monitorServices() {
        // Check health of all JARVIS services
        val services = listOf(
            "HotwordEngine" to JarvisCore.service.hotwordEngine.isHealthy(),
            "AutomationEngine" to JarvisCore.service.automationEngine.isHealthy()
        )
        
        services.forEach { (name, healthy) ->
            if (!healthy) {
                val event = MonitoringEvent(
                    type = "service",
                    message = "$name unhealthy",
                    timestamp = System.currentTimeMillis()
                )
                events.add(event)
                Logger.log("WARNING: $name is not healthy")
            }
        }
    }
    
    fun recordEvent(type: String, message: String, data: Map<String, Any> = emptyMap()) {
        events.add(MonitoringEvent(type, message, System.currentTimeMillis(), data))
        
        // Keep only last 1000 events
        if (events.size > 1000) {
            events.removeAt(0)
        }
    }
    
    private fun isUserAsleep(): Boolean {
        val hoursSinceActivity = (System.currentTimeMillis() - lastUserActivityTime) / (1000 * 60 * 60)
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        
        // User likely asleep if no activity for 4+ hours AND it's between 11 PM - 6 AM
        return hoursSinceActivity >= 4 && (currentHour >= 23 || currentHour <= 6)
    }
    
    fun updateUserActivity() {
        lastUserActivityTime = System.currentTimeMillis()
    }
    
    private fun generateAndDeliverSleepReport() {
        scope.launch(Dispatchers.Main) {
            // Wait until user wakes up (detects activity)
            while (isUserAsleep()) {
                delay(300000) // Check every 5 minutes
            }
            
            // User is awake, generate report
            val report = SleepReportGenerator.generate(events)
            
            // Deliver report
            delay(1800000) // Wait 30 minutes after waking
            JarvisCore.speak("Good morning. Here's your sleep report.")
            delay(2000)
            JarvisCore.speak(report)
            
            // Clear old events
            events.clear()
        }
    }
    
    fun getRecentEvents(count: Int = 50): List<MonitoringEvent> {
        return events.takeLast(count)
    }
}