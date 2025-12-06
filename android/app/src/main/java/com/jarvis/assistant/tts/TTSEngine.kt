package com.jarvis.assistant.tts

import android.content.Context
import android.media.MediaPlayer
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
    private var mediaPlayer: MediaPlayer? = null

    init {
        androidTTS = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            androidTTS?.language = Locale.US
            Logger.log("TTS Engine initialized successfully", Logger.Level.INFO)
        } else {
            Logger.log("TTS initialization failed with status: $status", Logger.Level.ERROR)
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
                playAudioFile(audioFile)
                Logger.log("ElevenLabs TTS completed", Logger.Level.INFO)
            } catch (e: Exception) {
                Logger.log("ElevenLabs TTS failed: ${e.message}", Logger.Level.ERROR)
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
            put("model_id", "eleven_monolingual_v1")
            put("voice_settings", JSONObject().apply {
                put("stability", if (isWhisperMode) 0.3 else 0.5)
                put("similarity_boost", if (isWhisperMode) 0.3 else 0.75)
            })
        }

        val body = json.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url(url)
            .addHeader("xi-api-key", Constants.ELEVENLABS_API_KEY)
            .addHeader("Content-Type", "application/json")
            .post(body)
            .build()

        val response = client.newCall(request).execute()
        if (!response.isSuccessful) {
            throw Exception("ElevenLabs API error: ${response.code} - ${response.message}")
        }
        
        val audioData = response.body?.bytes() ?: throw Exception("Empty response from ElevenLabs")
        val audioFile = File(context.cacheDir, "jarvis_speech_${System.currentTimeMillis()}.mp3")
        audioFile.writeBytes(audioData)
        
        Logger.log("Audio file generated: ${audioFile.absolutePath}", Logger.Level.DEBUG)
        audioFile
    }

    private fun playAudioFile(audioFile: File) {
        try {
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer().apply {
                setDataSource(audioFile.absolutePath)
                prepare()
                start()
                setOnCompletionListener {
                    audioFile.delete()
                    Logger.log("Audio playback completed", Logger.Level.DEBUG)
                }
                setOnErrorListener { _, what, extra ->
                    Logger.log("MediaPlayer error: what=$what, extra=$extra", Logger.Level.ERROR)
                    true
                }
            }
        } catch (e: Exception) {
            Logger.log("Failed to play audio file: ${e.message}", Logger.Level.ERROR)
            audioFile.delete()
        }
    }

    private fun speakWithAndroidTTS(text: String) {
        val volume = if (isWhisperMode) 0.3f else 1.0f
        val params = Bundle().apply {
            putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, volume)
        }
        androidTTS?.speak(text, TextToSpeech.QUEUE_ADD, params, null)
        Logger.log("Speaking with Android TTS: $text", Logger.Level.DEBUG)
    }
    
    fun setOnlineMode(enabled: Boolean) {
        useOnlineTTS = enabled
        Logger.log("Online TTS mode: $enabled", Logger.Level.INFO)
    }

    fun shutdown() {
        androidTTS?.shutdown()
        mediaPlayer?.release()
        scope.cancel()
        Logger.log("TTS Engine shutdown", Logger.Level.INFO)
    }
}
