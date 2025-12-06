package com.jarvis.assistant.modes

import android.content.Context
import com.jarvis.assistant.utils.Constants
import com.jarvis.assistant.utils.Logger

class LockdownManager(private val context: Context) {
    private var isEnabled = false

    fun enableLockdown() {
        isEnabled = true
        Logger.log("🔒 Lockdown ENABLED", Logger.Level.INFO)
    }

    fun disableLockdown() {
        isEnabled = false
        Logger.log("🔓 Lockdown disabled", Logger.Level.INFO)
    }

    fun shouldBlockCall(number: String): Boolean {
        if (!isEnabled) return false
        return Constants.LOCKDOWN_WHITELIST.none { it.second == number }
    }

    fun isActive(): Boolean = isEnabled
}
