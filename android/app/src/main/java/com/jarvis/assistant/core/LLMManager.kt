package com.jarvis.assistant.core

import android.content.Context
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
        Logger.log("LLM switched to: $llmName", Logger.Level.INFO)
    }

    suspend fun query(prompt: String, systemPrompt: String = ""): String = withContext(Dispatchers.IO) {
        try {
            when (currentLLM) {
                "gpt4-mini" -> queryOpenAI(prompt, "gpt-4-turbo", systemPrompt)
                "deepseek" -> queryDeepSeek(prompt, systemPrompt)
                "gemini" -> queryGemini(prompt, systemPrompt)
                else -> "Unknown LLM: $currentLLM"
            }
        } catch (e: Exception) {
            Logger.log("LLM query failed: ${e.message}", Logger.Level.ERROR)
            "Error processing request: ${e.message}"
        }
    }

    private suspend fun queryOpenAI(prompt: String, model: String, systemPrompt: String): String {
        val messages = JSONArray().apply {
            if (systemPrompt.isNotEmpty()) {
                put(JSONObject().put("role", "system").put("content", systemPrompt))
            }
            put(JSONObject().put("role", "user").put("content", prompt))
        }
        
        val json = JSONObject().apply {
            put("model", model)
            put("messages", messages)
            put("temperature", 0.7)
            put("max_tokens", 500)
        }

        val body = json.toString().toRequestBody("application/json".toMediaType())
        val request = Request.Builder()
            .url(Constants.OPENAI_ENDPOINT)
            .addHeader("Authorization", "Bearer ${Constants.OPENAI_API_KEY}")
            .addHeader("Content-Type", "application/json")
            .post(body)
            .build()

        val response = client.newCall(request).execute()
        val responseBody = response.body?.string() ?: return "Empty response from OpenAI"
        
        return try {
            val jsonResponse = JSONObject(responseBody)
            jsonResponse.getJSONArray("choices")
                .getJSONObject(0)
                .getJSONObject("message")
                .getString("content")
        } catch (e: Exception) {
            Logger.log("Failed to parse OpenAI response: ${e.message}", Logger.Level.ERROR)
            "Failed to parse response"
        }
    }

    private suspend fun queryDeepSeek(prompt: String, systemPrompt: String): String {
        return queryOpenAI(prompt, "deepseek-chat", systemPrompt)
    }
    
    private suspend fun queryGemini(prompt: String, systemPrompt: String): String {
        val fullPrompt = if (systemPrompt.isNotEmpty()) {
            "$systemPrompt\n\nUser: $prompt"
        } else {
            prompt
        }
        
        val json = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().put("text", fullPrompt))
                    })
                })
            })
        }
        
        val body = json.toString().toRequestBody("application/json".toMediaType())
        val url = "${Constants.GEMINI_ENDPOINT}?key=${Constants.GEMINI_API_KEY}"
        val request = Request.Builder()
            .url(url)
            .addHeader("Content-Type", "application/json")
            .post(body)
            .build()
        
        val response = client.newCall(request).execute()
        val responseBody = response.body?.string() ?: return "Empty response from Gemini"
        
        return try {
            val jsonResponse = JSONObject(responseBody)
            jsonResponse.getJSONArray("candidates")
                .getJSONObject(0)
                .getJSONObject("content")
                .getJSONArray("parts")
                .getJSONObject(0)
                .getString("text")
        } catch (e: Exception) {
            Logger.log("Failed to parse Gemini response: ${e.message}", Logger.Level.ERROR)
            "Failed to parse response"
        }
    }
}
