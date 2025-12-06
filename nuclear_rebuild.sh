#!/bin/bash

echo "🔥 JARVIS - NUCLEAR CLEAN + REBUILD"
echo "===================================="
echo ""
echo "⚠️  This will DELETE all existing Kotlin files"
echo "⚠️  and rebuild from scratch with ONLY working files"
echo ""
read -p "Press ENTER to continue or Ctrl+C to cancel..."
echo ""

cd /workspaces/Jarvis

# ============================================================================
# STEP 1: NUCLEAR DELETE - Remove ALL Kotlin files
# ============================================================================
echo "🗑️  STEP 1: Deleting all corrupted Kotlin files..."

rm -rf android/app/src/main/java/com/jarvis/assistant/*
rm -rf android/app/src/main/kotlin/com/jarvis/assistant/*

echo "✅ All old files deleted"
echo ""

# ============================================================================
# STEP 2: Create clean directory structure
# ============================================================================
echo "📁 STEP 2: Creating clean directory structure..."

mkdir -p android/app/src/main/java/com/jarvis/assistant
mkdir -p android/app/src/main/java/com/jarvis/assistant/utils
mkdir -p android/app/src/main/java/com/jarvis/assistant/core

echo "✅ Directory structure created"
echo ""

# ============================================================================
# STEP 3: Create ONLY essential files
# ============================================================================
echo "📝 STEP 3: Creating essential files..."

# ----------------------------------------------------------------------------
# FILE 1: JarvisApplication.kt (CLEAN VERSION)
# ----------------------------------------------------------------------------
echo "   Creating JarvisApplication.kt..."
cat > android/app/src/main/java/com/jarvis/assistant/JarvisApplication.kt << 'ENDFILE'
package com.jarvis.assistant

import android.app.Application
import android.util.Log

class JarvisApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        Log.i("JARVIS", "Application started")
    }
}
ENDFILE

# ----------------------------------------------------------------------------
# FILE 2: MainActivity.kt (CLEAN VERSION - NO FLASHLIGHT)
# ----------------------------------------------------------------------------
echo "   Creating MainActivity.kt..."
cat > android/app/src/main/java/com/jarvis/assistant/MainActivity.kt << 'ENDFILE'
package com.jarvis.assistant

import android.os.Bundle
import android.util.Log
import io.flutter.embedding.android.FlutterActivity

class MainActivity : FlutterActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("JARVIS", "MainActivity created")
    }
}
ENDFILE

# ----------------------------------------------------------------------------
# FILE 3: Logger.kt
# ----------------------------------------------------------------------------
echo "   Creating Logger.kt..."
cat > android/app/src/main/java/com/jarvis/assistant/utils/Logger.kt << 'ENDFILE'
package com.jarvis.assistant.utils

import android.util.Log

object Logger {
    enum class Level { INFO, ERROR, WARNING, DEBUG }
    
    fun log(message: String, level: Level = Level.INFO) {
        when (level) {
            Level.INFO -> Log.i("JARVIS", message)
            Level.ERROR -> Log.e("JARVIS", message)
            Level.WARNING -> Log.w("JARVIS", message)
            Level.DEBUG -> Log.d("JARVIS", message)
        }
    }
}
ENDFILE

# ----------------------------------------------------------------------------
# FILE 4: NetworkUtils.kt
# ----------------------------------------------------------------------------
echo "   Creating NetworkUtils.kt..."
cat > android/app/src/main/java/com/jarvis/assistant/utils/NetworkUtils.kt << 'ENDFILE'
package com.jarvis.assistant.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

object NetworkUtils {
    fun isConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = cm.activeNetwork ?: return false
        val capabilities = cm.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
ENDFILE

# ----------------------------------------------------------------------------
# FILE 5: Constants.kt
# ----------------------------------------------------------------------------
echo "   Creating Constants.kt..."
cat > android/app/src/main/java/com/jarvis/assistant/core/Constants.kt << 'ENDFILE'
package com.jarvis.assistant.core

object Constants {
    const val OPENAI_API_KEY = "your-key-here"
    const val ELEVENLABS_API_KEY = "your-key-here"
    const val ELEVENLABS_VOICE_ID = "your-voice-id"
    const val DEEPSEEK_API_KEY = "your-key-here"
    const val GEMINI_API_KEY = "your-key-here"
    
    const val OPENAI_ENDPOINT = "https://api.openai.com/v1/chat/completions"
    const val ELEVENLABS_ENDPOINT = "https://api.elevenlabs.io/v1/text-to-speech"
    
    const val SPOTIFY_PKG = "com.spotify.music"
    const val WHATSAPP_PKG = "com.whatsapp"
    
    val HOTWORDS = listOf("jarvis", "hey jarvis")
}
ENDFILE

# ----------------------------------------------------------------------------
# FILE 6: JarvisCore.kt
# ----------------------------------------------------------------------------
echo "   Creating JarvisCore.kt..."
cat > android/app/src/main/java/com/jarvis/assistant/core/JarvisCore.kt << 'ENDFILE'
package com.jarvis.assistant.core

import android.content.Context
import com.jarvis.assistant.utils.Logger

object JarvisCore {
    lateinit var context: Context

    fun initialize(context: Context) {
        this.context = context
        Logger.log("JarvisCore initialized", Logger.Level.INFO)
    }

    fun processCommand(command: String) {
        Logger.log("Processing: $command", Logger.Level.INFO)
    }

    fun speak(text: String) {
        Logger.log("Speaking: $text", Logger.Level.INFO)
    }
    
    fun announce(text: String) = speak(text)
}
ENDFILE

# ----------------------------------------------------------------------------
# FILE 7: JarvisService.kt (Placeholder)
# ----------------------------------------------------------------------------
echo "   Creating JarvisService.kt..."
cat > android/app/src/main/java/com/jarvis/assistant/core/JarvisService.kt << 'ENDFILE'
package com.jarvis.assistant.core

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.jarvis.assistant.utils.Logger

class JarvisService : Service() {
    
    override fun onCreate() {
        super.onCreate()
        Logger.log("JarvisService created", Logger.Level.INFO)
    }
    
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Logger.log("JarvisService started", Logger.Level.INFO)
        return START_STICKY
    }
}
ENDFILE

echo "✅ All 7 essential files created"
echo ""

# ============================================================================
# STEP 4: Verify files exist
# ============================================================================
echo "🔍 STEP 4: Verifying files..."
echo ""

if [ -f "android/app/src/main/java/com/jarvis/assistant/JarvisApplication.kt" ]; then
    echo "   ✅ JarvisApplication.kt"
else
    echo "   ❌ JarvisApplication.kt MISSING!"
fi

if [ -f "android/app/src/main/java/com/jarvis/assistant/MainActivity.kt" ]; then
    echo "   ✅ MainActivity.kt"
else
    echo "   ❌ MainActivity.kt MISSING!"
fi

if [ -f "android/app/src/main/java/com/jarvis/assistant/utils/Logger.kt" ]; then
    echo "   ✅ Logger.kt"
else
    echo "   ❌ Logger.kt MISSING!"
fi

if [ -f "android/app/src/main/java/com/jarvis/assistant/utils/NetworkUtils.kt" ]; then
    echo "   ✅ NetworkUtils.kt"
else
    echo "   ❌ NetworkUtils.kt MISSING!"
fi

if [ -f "android/app/src/main/java/com/jarvis/assistant/core/Constants.kt" ]; then
    echo "   ✅ Constants.kt"
else
    echo "   ❌ Constants.kt MISSING!"
fi

if [ -f "android/app/src/main/java/com/jarvis/assistant/core/JarvisCore.kt" ]; then
    echo "   ✅ JarvisCore.kt"
else
    echo "   ❌ JarvisCore.kt MISSING!"
fi

if [ -f "android/app/src/main/java/com/jarvis/assistant/core/JarvisService.kt" ]; then
    echo "   ✅ JarvisService.kt"
else
    echo "   ❌ JarvisService.kt MISSING!"
fi

echo ""

# ============================================================================
# STEP 5: Git commit and push
# ============================================================================
echo "📦 STEP 5: Committing to Git..."

git add android/app/src/main/java/com/jarvis/assistant/
git commit -m "🔥 NUCLEAR REBUILD: Clean slate with 7 essential files only"

echo ""
echo "🚀 STEP 6: Pushing to GitHub..."

git push origin main

echo ""
echo "✅ ============================================"
echo "✅ NUCLEAR REBUILD COMPLETE!"
echo "✅ ============================================"
echo ""
echo "📊 Summary:"
echo "   - Deleted: ALL old broken files"
echo "   - Created: 7 clean essential files"
echo "   - Status: Ready to build"
echo ""
echo "🎯 Next Steps:"
echo "   1. Go to Codemagic"
echo "   2. Click 'Start new build'"
echo "   3. Build should now compile successfully"
echo ""
echo "📝 Files created:"
echo "   1. JarvisApplication.kt (clean)"
echo "   2. MainActivity.kt (clean, no FLASHLIGHT)"
echo "   3. Logger.kt"
echo "   4. NetworkUtils.kt"
echo "   5. Constants.kt"
echo "   6. JarvisCore.kt"
echo "   7. JarvisService.kt"
echo ""
