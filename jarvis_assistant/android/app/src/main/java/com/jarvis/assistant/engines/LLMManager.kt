package com.jarvis.assistant.engines

import android.content.Context
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
                "gpt4-mini" -> queryOpenAI(prompt, "gpt-4-turbo-preview", systemPrompt)
                "gpt-o1" -> queryOpenAI(prompt, "o1-preview", systemPrompt)
                "deepseek" -> queryDeepSeek(prompt, systemPrompt)
                "gemini" -> queryGemini(prompt, systemPrompt)
                else -> "Unknown LLM selected"
            }
        } catch (e: Exception) {
            Logger.log("LLM query failed: ${e.message}")
            "I'm having trouble connecting to my AI systems. Please try again."
        }
    }
    
    private suspend fun queryOpenAI(prompt: String, model: String, systemPrompt: String): String {
        val messages = JSONArray().apply {
            if (systemPrompt.isNotEmpty()) {
                put(JSONObject().apply {
                    put("role", "system")
                    put("content", systemPrompt)
                })
            }
            put(JSONObject().apply {
                put("role", "user")
                put("content", prompt)
            })
        }
        
        val json = JSONObject().apply {
            put("model", model)
            put("messages", messages)
            put("max_tokens", 500)
            put("temperature", 0.7)
        }
        
        val body = json.toString().toRequestBody("application/json".toMediaType())
        
        val request = Request.Builder()
            .url(Constants.OPENAI_ENDPOINT)
            .addHeader("Authorization", "Bearer ${Constants.OPENAI_API_KEY}")
            .post(body)
            .build()
        
        val response = client.newCall(request).execute()
        val responseBody = response.body?.string() ?: return "Empty response"
        
        val responseJson = JSONObject(responseBody)
        return responseJson
            .getJSONArray("choices")
            .getJSONObject(0)
            .getJSONObject("message")
            .getString("content")
    }
    
    private suspend fun queryDeepSeek(prompt: String, systemPrompt: String): String {
        // Similar to OpenAI but with DeepSeek endpoint
        val messages = JSONArray().apply {
            if (systemPrompt.isNotEmpty()) {
                put(JSONObject().apply {
                    put("role", "system")
                    put("content", systemPrompt)
                })
            }
            put(JSONObject().apply {
                put("role", "user")
                put("content", prompt)
            })
        }
        
        val json = JSONObject().apply {
            put("model", "deepseek-chat")
            put("messages", messages)
            put("max_tokens", 500)
        }
        
        val body = json.toString().toRequestBody("application/json".toMediaType())
        
        val request = Request.Builder()
            .url(Constants.DEEPSEEK_ENDPOINT)
            .addHeader("Authorization", "Bearer ${Constants.DEEPSEEK_API_KEY}")
            .post(body)
            .build()
        
        val response = client.newCall(request).execute()
        val responseBody = response.body?.string() ?: return "Empty response"
        
        val responseJson = JSONObject(responseBody)
        return responseJson
            .getJSONArray("choices")
            .getJSONObject(0)
            .getJSONObject("message")
            .getString("content")
    }
    
    private suspend fun queryGemini(prompt: String, systemPrompt: String): String {
        val fullPrompt = if (systemPrompt.isNotEmpty()) {
            "$systemPrompt\n\n$prompt"
        } else {
            prompt
        }
        
        val json = JSONObject().apply {
            put("contents", JSONArray().apply {
                put(JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply {
                            put("text", fullPrompt)
                        })
                    })
                })
            })
        }
        
        val body = json.toString().toRequestBody("application/json".toMediaType())
        
        val request = Request.Builder()
            .url("${Constants.GEMINI_ENDPOINT}?key=${Constants.GEMINI_API_KEY}")
            .post(body)
            .build()
        
        val response = client.newCall(request).execute()
        val responseBody = response.body?.string() ?: return "Empty response"
        
        val responseJson = JSONObject(responseBody)
        return responseJson
            .getJSONArray("candidates")
            .getJSONObject(0)
            .getJSONObject("content")
            .getJSONArray("parts")
            .getJSONObject(0)
            .getString("text")
    }
}