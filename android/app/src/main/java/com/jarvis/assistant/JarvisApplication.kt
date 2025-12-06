package com.jarvis.assistant

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.ScrollView
import android.widget.TextView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.jarvis.assistant.core.JarvisCore
import com.jarvis.assistant.core.JarvisService
import com.jarvis.assistant.utils.Logger
import com.jarvis.assistant.modes.PersonalityManager

class MainActivity : AppCompatActivity() {
    
    private lateinit var statusText: TextView
    private lateinit var logsText: TextView
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Create UI programmatically
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 32, 32, 32)
        }
        
        // Status display
        statusText = TextView(this).apply {
            text = "JARVIS Status: Initializing..."
            textSize = 16f
            setPadding(0, 0, 0, 24)
        }
        layout.addView(statusText)
        
        // Control buttons
        addButton(layout, "Start JARVIS Service") {
            startJarvisService()
        }
        
        addButton(layout, "Stop JARVIS Service") {
            stopJarvisService()
        }
        
        addButton(layout, "Test Voice Command") {
            JarvisCore.processCommand("Jarvis, what time is it?")
        }
        
        addButton(layout, "Switch to Combat Mode") {
            JarvisCore.service.personalityManager.switchMode(PersonalityManager.PersonalityMode.COMBAT)
        }
        
        addButton(layout, "Request Permissions") {
            requestAllPermissions()
        }
        
        // Logs display
        val scrollView = ScrollView(this).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
            ).apply {
                topMargin = 24
            }
        }
        
        logsText = TextView(this).apply {
            text = "Logs will appear here..."
            textSize = 12f
            setBackgroundColor(0xFF222222.toInt())
            setTextColor(0xFF00E0FF.toInt())
            setPadding(16, 16, 16, 16)
        }
        
        scrollView.addView(logsText)
        layout.addView(scrollView)
        
        setContentView(layout)
        
        // Start service automatically
        startJarvisService()
        
        // Update logs every 2 seconds
        updateLogs()
    }
    
    private fun addButton(parent: LinearLayout, text: String, onClick: () -> Unit) {
        Button(this).apply {
            this.text = text
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                bottomMargin = 16
            }
            setOnClickListener { onClick() }
            parent.addView(this)
        }
    }
    
    private fun startJarvisService() {
        val intent = Intent(this, JarvisService::class.java)
        ContextCompat.startForegroundService(this, intent)
        statusText.text = "JARVIS Status: Service Started"
        Logger.log("MainActivity started JARVIS service", Logger.Level.INFO)
    }
    
    private fun stopJarvisService() {
        val intent = Intent(this, JarvisService::class.java)
        stopService(intent)
        statusText.text = "JARVIS Status: Service Stopped"
    }
    
    private fun updateLogs() {
        logsText.postDelayed({
            val logs = Logger.getRecentLogs(50)
            logsText.text = logs.joinToString("\n") { 
                "${it.level}: ${it.message}" 
            }
            updateLogs()
        }, 2000)
    }
    
    private fun requestAllPermissions() {
        val permissions = arrayOf(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.FLASHLIGHT
        )
        
        ActivityCompat.requestPermissions(this, permissions, 100)
    }
}
            permissionLauncher.launch(requiredPermissions)
        } else {
            startJarvis()
        }
    }

    private fun hasAllPermissions(): Boolean {
        return requiredPermissions.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun startJarvis() {
        val intent = Intent(this, JarvisService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }
}
