package com.jarvis.assistant.monitoring

data class MonitoringEvent(
    val timestamp: Long,
    val type: String,
    val value: Float? = null
)

class SleepReportGenerator {

    private val events = mutableListOf<MonitoringEvent>()

    fun logEvent(type: String, value: Float? = null) {
        events.add(
            MonitoringEvent(
                timestamp = System.currentTimeMillis(),
                type = type,
                value = value
            )
        )
    }

    fun generateReport(): String {
        if (events.isEmpty()) return "No sleep data collected."

        val start = events.first().timestamp
        val end = events.last().timestamp
        val durationMinutes = (end - start) / 60000

        val movements = events.count { it.type == "movement" }

        return """
            Sleep Report:
            • Duration: $durationMinutes minutes
            • Movements detected: $movements
            • Total events: ${events.size}
        """.trimIndent()
    }
}
