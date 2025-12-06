package com.jarvis.assistant.core

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.*
import androidx.core.app.NotificationCompat
import com.jarvis.assistant.engines.HotwordEngine
import com.jarvis.assistant.modes.*
import com.jarvis.assistant.tts.TTSEngine
import com.jarvis.assistant.utils.Logger
import kotlinx.coroutines.*

class JarvisService : Service() {
    private lateinit var wakeLock: PowerManager.WakeLock
    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    lateinit var llmManager: LLMManager
    lateinit var ttsEngine: TTSEngine
    lateinit var personalityManager: PersonalityManager
    lateinit var workModeManager: WorkModeManager
    lateinit var lockdownManager: LockdownManager
    lateinit var mediaController: MediaController
    lateinit var hotwordEngine: HotwordEngine  // ← NEW: Hotword detection

    inner class LocalBinder : Binder() {
        fun getService(): JarvisService = this@JarvisService
    }

    override fun onCreate() {
        super.onCreate()
        Logger.log("JARVIS Service starting...", Logger.Level.INFO)

        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "JARVIS::WakeLock")
        wakeLock.acquire()

        // Initialize core components
        llmManager = LLMManager(this)
        ttsEngine = TTSEngine(this)
        personalityManager = PersonalityManager()
        workModeManager = WorkModeManager(this)
        lockdownManager = LockdownManager(this)
        mediaController = MediaController(this)
        
        // Initialize Hotword Engine
        hotwordEngine = HotwordEngine(this)

        JarvisCore.initialize(this)
        startForeground(1, createNotification())

        // Start hotword detection after 2 seconds (let TTS initialize first)
        serviceScope.launch {
            delay(2000)
            hotwordEngine.start()
        }

        Logger.log("✅ JARVIS Service online with hotword detection", Logger.Level.INFO)
    }

    private fun createNotification(): Notification {
        val channelId = "jarvis_service"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "JARVIS Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "JARVIS AI Assistant - Always Listening"
            }
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("JARVIS Online")
            .setContentText("Hey Jarvis - Always Listening")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
    }

    fun speak(text: String) {
        serviceScope.launch {
            ttsEngine.speak(text, personalityManager.getResponseStyle())
        }
    }

    override fun onBind(intent: Intent): IBinder = LocalBinder()

    override fun onDestroy() {
        super.onDestroy()
        
        // Stop hotword detection
        if (::hotwordEngine.isInitialized) {
            hotwordEngine.stop()
        }
        
        wakeLock.release()
        ttsEngine.shutdown()
        serviceScope.cancel()
        
        Logger.log("JARVIS Service stopped", Logger.Level.WARNING)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY  // Auto-restart if killed
    }
}

        // Initialize core components
        llmManager = LLMManager(this)
        ttsEngine = TTSEngine(this)
        personalityManager = PersonalityManager()
        workModeManager = WorkModeManager(this)
        lockdownManager = LockdownManager(this)
        mediaController = MediaController(this)
        
        // Initialize Hotword Engine
        hotwordEngine = HotwordEngine(this)

        JarvisCore.initialize(this)
        startForeground(1, createNotification())

        // Start hotword detection after 2 seconds (let TTS initialize first)
        serviceScope.launch {
            delay(2000)
            hotwordEngine.start()
        }

        Logger.log("✅ JARVIS Service online with hotword detection", Logger.Level.INFO)
    }

    private fun createNotification(): Notification {
        val channelId = "jarvis_service"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "JARVIS Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "JARVIS AI Assistant - Always Listening"
            }
            getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
        }

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("JARVIS Online")
            .setContentText("Hey Jarvis - Always Listening")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
    }

    fun speak(text: String) {
        serviceScope.launch {
            ttsEngine.speak(text, personalityManager.getResponseStyle())
        }
    }

    override fun onBind(intent: Intent): IBinder = LocalBinder()

    override fun onDestroy() {
        super.onDestroy()
        
        // Stop hotword detection
        if (::hotwordEngine.isInitialized) {
            hotwordEngine.stop()
        }
        
        wakeLock.release()
        ttsEngine.shutdown()
        serviceScope.cancel()
        
        Logger.log("JARVIS Service stopped", Logger.Level.WARNING)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY  // Auto-restart if killed
    }
}
