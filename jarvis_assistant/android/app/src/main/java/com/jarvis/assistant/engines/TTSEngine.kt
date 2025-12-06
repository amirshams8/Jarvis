package com.jarvis.assistant.engines

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
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
            androidTTS?.setPitch(1.0f)
            androidTTS?.setSpeechRate(1.0f)
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
            } catch (e: Exception) {
                Logger.log("ElevenLabs failed, falling back to Android TTS: ${e.message}")
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
                put("stability", 0.5)
                put("similarity_boost", 0.75)
            })
        }
        
        val body = json.toString().toRequestBody("application/json".toMediaType())
        
        val request = Request.Builder()
            .url(url)
            .addHeader("xi-api-key", Constants.ELEVENLABS_API_KEY)
            .post(body)
            .build()
        
        val response = client.newCall(request).execute()
        
        if (!response.isSuccessful) {
            throw Exception("ElevenLabs API failed: ${response.code}")
        }
        
        val audioData = response.body?.bytes() ?: throw Exception("Empty response")
        
        val audioFile = File(context.cacheDir, "jarvis_speech_${System.currentTimeMillis()}.mp3")
        audioFile.writeBytes(audioData)
        
        audioFile
    }
    
    private fun playAudioFile(file: File) {
        val mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .setUsage(AudioAttributes.USAGE_ASSISTANT)
                    .build()
            )
            setDataSource(file.absolutePath)
            setVolume(if (isWhisperMode) 0.3f else 1.0f, if (isWhisperMode) 0.3f else 1.0f)
            setOnCompletionListener {
                it.release()
                file.delete()
            }
            prepare()
            start()
        }
    }
    
    private fun speakWithAndroidTTS(text: String) {
        val volume = if (isWhisperMode) 0.3f else 1.0f
        
        val params = Bundle().apply {
            putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, volume)
        }
        
        androidTTS?.speak(text, TextToSpeech.QUEUE_ADD, params, null)
    }
    
    fun setOnlineMode(online: Boolean) {
        useOnlineTTS = online
    }
    
    fun shutdown() {
        androidTTS?.shutdown()
        scope.cancel()
    }
}