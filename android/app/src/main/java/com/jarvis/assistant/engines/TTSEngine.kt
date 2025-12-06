package com.jarvis.assistant.engines

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import com.jarvis.assistant.core.Constants
import com.jarvis.assistant.utils.Logger
import com.jarvis.assistant.utils.NetworkUtils
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.File
import java.util.*

class TTSEngine(private val context: Context) : TextToSpeech.OnInitListener {
    private var androidTTS: TextToSpeech? = null
    private var useOnlineTTS = true
    private var isWhisperMode = false
    private val client = OkHttpClient()
    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        androidTTS = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            androidTTS?.language = Locale.US
        }
    }

    fun speak(text: String, whisper: Boolean = false) {
        isWhisperMode = whisper
        if (useOnlineTTS && NetworkUtils.isConnected(context)) {
            speakWithElevenLabs(text)
        } else {
            speakWithAndroidTTS(text)
        }
    }

    private fun speakWithElevenLabs(text: String) {
        scope.launch {
            try {
                val audioFile = generateSpeechElevenLabs(text)
                Logger.log("Generated speech")
            } catch (e: Exception) {
                Logger.log("ElevenLabs failed: ${e.message}")
                withContext(Dispatchers.Main) {
                    speakWithAndroidTTS(text)
                }
            }
        }
    }

    private suspend fun generateSpeechElevenLabs(text: String): File = withContext(Dispatchers.IO) {
        val url = "${Constants.ELEVENLABS_ENDPOINT}/${Constants.ELEVENLABS_VOICE_ID}"
        val json = JSONObject().apply {
            put("text", text)
        }

        val body = json.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url(url)
            .addHeader("xi-api-key", Constants.ELEVENLABS_API_KEY)
            .post(body)
            .build()

        val response = client.newCall(request).execute()
        val audioData = response.body?.bytes() ?: throw Exception("Empty response")
        val audioFile = File(context.cacheDir, "jarvis_speech.mp3")
        audioFile.writeBytes(audioData)
        audioFile
    }

    private fun speakWithAndroidTTS(text: String) {
        val volume = if (isWhisperMode) 0.3f else 1.0f
        val params = Bundle()
        params.putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, volume)
        androidTTS?.speak(text, TextToSpeech.QUEUE_ADD, params, null)
    }

    fun shutdown() {
        androidTTS?.shutdown()
        scope.cancel()
    }
}
