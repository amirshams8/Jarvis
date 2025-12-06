package com.jarvis.assistant.automation

import android.content.Context
import android.telecom.TelecomManager
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import kotlinx.coroutines.*

class CallHandler(private val context: Context) {
    
    private var telephonyManager: TelephonyManager? = null
    private var phoneStateListener: PhoneStateListener? = null
    private var autoAnswerEnabled = false
    private val scope = CoroutineScope(Dispatchers.Main)
    
    fun initialize() {
        telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        
        phoneStateListener = object : PhoneStateListener() {
            override fun onCallStateChanged(state: Int, phoneNumber: String?) {
                super.onCallStateChanged(state, phoneNumber)
                
                when (state) {
                    TelephonyManager.CALL_STATE_RINGING -> {
                        handleIncomingCall(phoneNumber)
                    }
                    TelephonyManager.CALL_STATE_OFFHOOK -> {
                        // Call answered
                        Logger.log("Call answered")
                    }
                    TelephonyManager.CALL_STATE_IDLE -> {
                        // Call ended
                        Logger.log("Call ended")
                    }
                }
            }
        }
        
        telephonyManager?.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)
    }
    
    private fun handleIncomingCall(number: String?) {
        number?.let {
            Logger.log("Incoming call from: $it")
            
            // Check lockdown mode
            if (JarvisCore.service.lockdownManager.isLockdownActive) {
                // Handled by LockdownManager
                return
            }
            
            // Auto-answer if enabled
            if (autoAnswerEnabled) {
                scope.launch {
                    delay(2000) // Wait 2 seconds
                    answerCall()
                }
            }
            
            // Announce caller
            JarvisCore.announce("Incoming call from $it")
        }
    }
    
    fun answerCall() {
        try {
            val telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
            telecomManager.acceptRingingCall()
            JarvisCore.speak("Call answered")
        } catch (e: Exception) {
            Logger.log("Failed to answer call: ${e.message}")
        }
    }
    
    fun rejectCall() {
        try {
            val telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager
            telecomManager.endCall()
            JarvisCore.speak("Call rejected")
        } catch (e: Exception) {
            Logger.log("Failed to reject call: ${e.message}")
        }
    }
    
    fun enableAutoAnswer() {
        autoAnswerEnabled = true
        JarvisCore.speak("Auto answer enabled")
    }
    
    fun disableAutoAnswer() {
        autoAnswerEnabled = false
        JarvisCore.speak("Auto answer disabled")
    }
    
    fun cleanup() {
        phoneStateListener?.let {
            telephonyManager?.listen(it, PhoneStateListener.LISTEN_NONE)
        }
    }
}
