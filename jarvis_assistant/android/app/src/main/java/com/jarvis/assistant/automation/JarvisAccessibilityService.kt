package com.jarvis.assistant.automation

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import com.jarvis.assistant.utils.Logger

class JarvisAccessibilityService : AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        event?.let {
            Logger.log("Accessibility: ${it.packageName}", Logger.Level.DEBUG)
        }
    }

    override fun onInterrupt() {
        Logger.log("Accessibility service interrupted", Logger.Level.WARNING)
    }
}
