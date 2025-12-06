package com.jarvis.assistant.engines

import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import kotlinx.coroutines.*
import java.nio.ByteBuffer
import java.nio.ByteOrder

class HotwordEngine(private val context: Context) {
    
    private var isListening = false
    private var audioRecord: AudioRecord? = null
    private val scope = CoroutineScope(Dispatchers.IO)
    private var whisperDetector: WhisperDetector? = null
    private var lastDetectionTime = 0L
    
    fun start() {
        if (isListening) return
        
        isListening = true
        whisperDetector = WhisperDetector(context)
        
        scope.launch {
            startListeningLoop()
        }
        
        Logger.log("Hotword Engine Started - Always Listening")
    }
    
    private suspend fun startListeningLoop() {
        val sampleRate = 16000
        val bufferSize = AudioRecord.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        ) * 2
        
        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        )
        
        audioRecord?.startRecording()
        
        val buffer = ShortArray(bufferSize / 2)
        
        while (isListening) {
            try {
                val read = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                
                if (read > 0) {
                    processAudioBuffer(buffer.copyOf(read))
                }
                
            } catch (e: Exception) {
                Logger.log("Hotword detection error: ${e.message}")
                delay(1000)
            }
        }
    }
    
    private suspend fun processAudioBuffer(buffer: ShortArray) {
        // Rate limiting - don't detect too frequently
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastDetectionTime < 2000) return
        
        val transcription = whisperDetector?.transcribe(buffer) ?: return
        
        val detected = Constants.HOTWORDS.any { 
            transcription.contains(it, ignoreCase = true)
        }
        
        if (detected) {
            lastDetectionTime = currentTime
            onHotwordDetected()
        }
    }
    
    private fun onHotwordDetected() {
        Logger.log("Hotword detected!")
        
        // Stop recording temporarily
        val wasListening = isListening
        stop()
        
        // Start full speech recognition
        scope.launch(Dispatchers.Main) {
            SpeechRecognitionEngine.startListening(context) { command ->
                JarvisCore.processCommand(command)
                
                // Resume hotword detection
                if (wasListening) {
                    start()
                }
            }
        }
    }
    
    fun stop() {
        isListening = false
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
    }
    
    fun isHealthy(): Boolean = isListening && audioRecord?.recordingState == AudioRecord.RECORDSTATE_RECORDING
    
    fun restart() {
        stop()
        delay(500)
        start()
    }
}

class WhisperDetector(private val context: Context) {
    
    // TODO: Implement Whisper model integration
    // Using Whisper.cpp or ONNX Runtime
    
    suspend fun transcribe(audio: ShortArray): String = withContext(Dispatchers.Default) {
        try {
            // Convert audio to format Whisper expects
            val floatAudio = audio.map { it / 32768.0f }.toFloatArray()
            
            // Run inference
            // val result = whisperModel.transcribe(floatAudio)
            
            // For now, return empty (replace with actual Whisper implementation)
            ""
        } catch (e: Exception) {
            Logger.log("Whisper transcription failed: ${e.message}")
            ""
        }
    }
}
