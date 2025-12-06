package com.jarvis.assistant.monitoring

object SleepReportGenerator {
    
    fun generate(events: List<IntelligentMonitor.MonitoringEvent>): String {
        val callEvents = events.filter { it.type == "call" }
        val messageEvents = events.filter { it.type == "message" }
        val batteryEvents = events.filter { it.type == "battery" }
        val alertEvents = events.filter { it.type == "alert" }
        
        val report = buildString {
            append("While you were asleep: ")
            
            if (callEvents.isNotEmpty()) {
                append("You received ${callEvents.size} calls. ")
            }
            
            if (messageEvents.isNotEmpty()) {
                append("${messageEvents.size} messages. ")
            }
            
            if (batteryEvents.isNotEmpty()) {
                append("Battery dropped to ${batteryEvents.last().data["level"]}%. ")
            }
            
            if (alertEvents.isNotEmpty()) {
                append("${alertEvents.size} alerts were triggered. ")
            }
            
            if (events.isEmpty()) {
                append("Everything was quiet. No significant events.")
            }
        }
        
        return report.toString()
    }
}
