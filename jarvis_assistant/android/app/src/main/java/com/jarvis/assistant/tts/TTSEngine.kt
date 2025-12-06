package com.jarvis.assistant.tts

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.speech.tts.TextToSpeech
import com.jarvis.assistant.modes.PersonalityManager
import com.jarvis.assistant.utils.Constants
import com.jarvis.assistant.utils.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONObject
import java.io.File
import java.util.*

class TTSEngine(private val context: Context) : TextToSpeech.OnInitListener {

    private var androidTTS: TextToSpeech? = null
    private var mediaPlayer: MediaPlayer? = null
    private val client = OkHttpClient.Builder()
        .connectTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    private var isAndroidTTSReady = false

    init {
        androidTTS = TextToSpeech(context, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            androidTTS?.language = Locale.US
            androidTTS?.setSpeechRate(Constants.TTS_SPEAKING_RATE)
            androidTTS?.setPitch(Constants.TTS_PITCH)
            isAndroidTTSReady = true
            Logger.log("✅ Android TTS initialized (backup)", Logger.Level.INFO)
        }
    }

    suspend fun speak(text: String, style: PersonalityManager.ResponseStyle) {
        val finalText = style.prefix + text
        Logger.log("🎙️ JARVIS: $finalText", Logger.Level.INFO)

        if (Constants.USE_ELEVENLABS && Constants.ELEVENLABS_API_KEY.startsWith("xi-")) {
            if (speakElevenLabs(finalText, style)) return
        }

        speakAndroidTTS(finalText, style)
    }

    private suspend fun speakElevenLabs(text: String, style: PersonalityManager.ResponseStyle): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val json = JSONObject().apply {
                    put("text", text.take(4900))
                    put("model_id", Constants.ELEVENLABS_MODEL_ID)
                    put("voice_settings", JSONObject().apply {
                        put("stability", if (style.urgent) 0.3f else Constants.VOICE_STABILITY)
                        put("similarity_boost", Constants.VOICE_SIMILARITY)
                        put("style", if (style.whisper) 0.3f else Constants.VOICE_STYLE)
                    })
                }

                val request = Request.Builder()
                    .url("https://api.elevenlabs.io/v1/text-to-speech/${Constants.ELEVENLABS_VOICE_ID}")
                    .post(json.toString().toRequestBody("application/json".toMediaTypeOrNull()))
                    .addHeader("xi-api-key", Constants.ELEVENLABS_API_KEY)
                    .build()

                val response = client.newCall(request).execute()

                if (response.isSuccessful) {
                    val audioBytes = response.body?.bytes()
                    if (audioBytes != null && audioBytes.isNotEmpty()) {
                        playAudio(audioBytes)
                        Logger.log("⭐ ELEVENLABS SUCCESS", Logger.Level.INFO)
                        true
                    } else false
                } else {
                    Logger.log("❌ ElevenLabs ${response.code}", Logger.Level.WARNING)
                    false
                }
            } catch (e: Exception) {
                Logger.log("❌ ElevenLabs: ${e.message}", Logger.Level.ERROR)
                false
            }
        }
    }

    private suspend fun playAudio(audioBytes: ByteArray) {
        withContext(Dispatchers.Main) {
            try {
                mediaPlayer?.release()
                mediaPlayer = MediaPlayer().apply {
                    setAudioAttributes(
                        AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                            .build()
                    )
                    val tempFile = File(context.cacheDir, "jarvis_voice.mp3")
                    tempFile.writeBytes(audioBytes)
                    setDataSource(tempFile.absolutePath)
                    prepare()
                    setOnCompletionListener { it.release() }
                    start()
                }
            } catch (e: Exception) {
                Logger.log("❌ Audio error: ${e.message}", Logger.Level.ERROR)
            }
        }
    }

    private fun speakAndroidTTS(text: String, style: PersonalityManager.ResponseStyle) {
        if (!isAndroidTTSReady) return
        androidTTS?.let { tts ->
            tts.setSpeechRate(if (style.whisper) 0.75f else 1.0f)
            tts.setPitch(if (style.urgent) 1.2f else 1.0f)
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "jarvis")
            Logger.log("📱 Android TTS fallback", Logger.Level.INFO)
        }
    }

    fun stop() {
        androidTTS?.stop()
        mediaPlayer?.stop()
    }

    fun shutdown() {
        androidTTS?.shutdown()
        mediaPlayer?.release()
    }
}
