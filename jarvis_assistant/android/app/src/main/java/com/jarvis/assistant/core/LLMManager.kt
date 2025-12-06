package com.jarvis.assistant.core

import android.content.Context
import com.jarvis.assistant.utils.Constants
import com.jarvis.assistant.utils.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class LLMManager(private val context: Context) {
    private var currentLLM = Constants.DEFAULT_LLM

    suspend fun query(prompt: String, model: String? = null): String = 
        withContext(Dispatchers.IO) {
            val targetModel = model ?: currentLLM

            if (!Constants.ALL_LLMS.contains(targetModel)) {
                return@withContext "Model not available: $targetModel"
            }

            val json = JSONObject().apply {
                put("messages", JSONArray().apply {
                    put(JSONObject().apply {
                        put("role", "user")
                        put("content", "You are JARVIS from Iron Man. Respond concisely: $prompt")
                    })
                })
                put("model", targetModel)
                put("stream", false)
                put("temperature", 0.7)
                put("max_tokens", 1500)
            }

            try {
                val client = OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(90, TimeUnit.SECONDS)
                    .build()

                val request = Request.Builder()
                    .url(Constants.PUTER_AI_URL)
                    .post(json.toString().toRequestBody("application/json".toMediaTypeOrNull()))
                    .addHeader("User-Agent", "JARVIS-Android/3.1")
                    .build()

                val response = client.newCall(request).execute()
                val content = parsePuterResponse(response.body?.string() ?: "")

                Logger.log("[$targetModel] $content", Logger.Level.INFO)
                content

            } catch (e: Exception) {
                Logger.log("AI error: ${e.message}", Logger.Level.ERROR)
                "AI service error: ${e.message}"
            }
        }

    fun switchLLM(modelName: String) {
        if (Constants.ALL_LLMS.contains(modelName)) {
            currentLLM = modelName
            JarvisCore.speak("Switched to $modelName")
            Logger.log("LLM → $modelName", Logger.Level.INFO)
        }
    }

    fun getCurrentLLM(): String = currentLLM
    fun getAvailableModels(): List<String> = Constants.ALL_LLMS

    private fun parsePuterResponse(raw: String): String {
        return try {
            val json = JSONObject(raw)
            when {
                json.has("choices") -> {
                    json.getJSONArray("choices").getJSONObject(0)
                        .getJSONObject("message").getString("content")
                }
                json.has("message") -> json.getJSONObject("message").getString("content")
                json.has("text") -> json.getString("text")
                else -> raw.take(300)
            }
        } catch (e: Exception) {
            raw.take(200)
        }
    }
}
