package com.jarvis.assistant.engines

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import com.jarvis.assistant.utils.Logger

class SpeechRecognitionEngine(private val context: Context) : RecognitionListener {
    
    private var speechRecognizer: SpeechRecognizer? = null
    private var onResultCallback: ((String) -> Unit)? = null
    
    init {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        speechRecognizer?.setRecognitionListener(this)
    }
    
    fun startListening(callback: (String) -> Unit) {
        onResultCallback = callback
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
        }
        speechRecognizer?.startListening(intent)
        Logger.log("Speech recognition started", Logger.Level.INFO)
    }
    
    fun stopListening() {
        speechRecognizer?.stopListening()
    }
    
    override fun onResults(results: Bundle?) {
        val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        if (matches != null && matches.isNotEmpty()) {
            val recognizedText = matches[0]
            Logger.log("Recognized: $recognizedText", Logger.Level.INFO)
            onResultCallback?.invoke(recognizedText)
        }
    }
    
    override fun onError(error: Int) {
        Logger.log("Speech recognition error: $error", Logger.Level.ERROR)
    }
    
    override fun onReadyForSpeech(params: Bundle?) {}
    override fun onBeginningOfSpeech() {}
    override fun onRmsChanged(rmsdB: Float) {}
    override fun onBufferReceived(buffer: ByteArray?) {}
    override fun onEndOfSpeech() {}
    override fun onPartialResults(partialResults: Bundle?) {}
    override fun onEvent(eventType: Int, params: Bundle?) {}
    
    fun destroy() {
        speechRecognizer?.destroy()
    }
}
