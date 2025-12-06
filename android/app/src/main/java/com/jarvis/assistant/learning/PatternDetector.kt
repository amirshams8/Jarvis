package com.jarvis.assistant.learning

import android.content.Context
import java.util.*

class PatternDetector(private val context: Context) {
    
    data class Pattern(
        val sequence: List<String>,
        val frequency: Int,
        val timeOfDay: IntRange,
        val confidence: Float
    )
    
    fun detectSequentialPatterns(events: List<LearningSystem.CommandEvent>): List<Pattern> {
        val patterns = mutableListOf<Pattern>()
        
        // Group events by hour
        val hourlyGroups = events.groupBy { it.hourOfDay }
        
        hourlyGroups.forEach { (hour, eventsInHour) ->
            if (eventsInHour.size >= 3) {
                // Find sequences that repeat
                val sequences = findRepeatingSequences(eventsInHour)
                
                sequences.forEach { (seq, count) ->
                    if (count >= 2) { // Sequence must repeat at least twice
                        patterns.add(Pattern(
                            sequence = seq,
                            frequency = count,
                            timeOfDay = (hour - 1)..(hour + 1),
                            confidence = count.toFloat() / eventsInHour.size
                        ))
                    }
                }
            }
        }
        
        return patterns.sortedByDescending { it.confidence }
    }
    
    private fun findRepeatingSequences(events: List<LearningSystem.CommandEvent>): Map<List<String>, Int> {
        val sequenceMap = mutableMapOf<List<String>, Int>()
        
        // Look for sequences of length 2-4
        for (seqLength in 2..4) {
            for (i in 0..(events.size - seqLength)) {
                val sequence = events.subList(i, i + seqLength).map { it.command }
                sequenceMap[sequence] = sequenceMap.getOrDefault(sequence, 0) + 1
            }
        }
        
        return sequenceMap.filter { it.value > 1 }
    }
    
    fun detectAnomalies(events: List<LearningSystem.CommandEvent>): List<String> {
        val anomalies = mutableListOf<String>()
        
        // Detect unusual times
        val unusualHourCommands = events.filter { 
            it.hourOfDay in 2..5 // 2 AM - 5 AM
        }
        
        if (unusualHourCommands.isNotEmpty()) {
            anomalies.add("Unusual activity detected between 2-5 AM")
        }
        
        // Detect repeated failures
        val failedCommands = events.filter { !it.success }
        if (failedCommands.size > events.size * 0.3) {
            anomalies.add("High failure rate detected: ${failedCommands.size} failures")
        }
        
        return anomalies
    }
    
    fun suggestOptimizations(events: List<LearningSystem.CommandEvent>): List<String> {
        val suggestions = mutableListOf<String>()
        
        // Analyze command efficiency
        val commandTimes = events.groupBy { it.command }
        
        commandTimes.forEach { (command, cmdEvents) ->
            if (cmdEvents.size > 10) {
                // Suggest automation
                suggestions.add("Consider automating '$command' - used ${cmdEvents.size} times")
            }
        }
        
        return suggestions
    }
}