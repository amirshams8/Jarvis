package com.jarvis.assistant.engines

import android.content.Context
import com.jarvis.assistant.core.Constants
import com.jarvis.assistant.core.JarvisCore
import com.jarvis.assistant.utils.Logger
import kotlinx.coroutines.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

class LLMManager(private val context: Context) {
    private var currentLLM = "gpt4-mini"
    private val client = OkHttpClient()

    fun switchLLM(llmName: String) {
        currentLLM = llmName
        JarvisCore.speak("Switched to $llmName")
        Logger.log("LLM switched to: $llmName")
    }

    suspend fun query(prompt: String, systemPrompt: String = ""): String = withContext(Dispatchers.IO) {
        try {
            when (currentLLM) {
                "gpt4-mini" -> queryOpenAI(prompt, "gpt-4-turbo", systemPrompt)
                "deepseek" -> queryDeepSeek(prompt, systemPrompt)
                else -> "Unknown LLM"
            }
        } catch (e: Exception) {
            Logger.log("LLM query failed: ${e.message}")
            "Error processing request"
        }
    }

    private suspend fun queryOpenAI(prompt: String, model: String, systemPrompt: String): String {
        val messages = JSONArray()
        val json = JSONObject().apply {
            put("model", model)
            put("messages", messages)
        }

        val body = json.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url(Constants.OPENAI_ENDPOINT)
            .addHeader("Authorization", "Bearer ${Constants.OPENAI_API_KEY}")
            .post(body)
            .build()

        val response = client.newCall(request).execute()
        return response.body?.string() ?: "Empty response"
    }

    private suspend fun queryDeepSeek(prompt: String, systemPrompt: String): String {
        return queryOpenAI(prompt, "deepseek-chat", systemPrompt)
    }
}
