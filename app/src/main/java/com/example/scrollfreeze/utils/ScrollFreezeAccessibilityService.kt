package com.example.scrollfreeze.utils

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Intent
import android.os.CountDownTimer
import android.view.accessibility.AccessibilityEvent
import android.widget.Toast
import com.example.scrollfreeze.data.local.database.AppDataHolder

class ScrollFreezeAccessibilityService : AccessibilityService() {

    private var timer: CountDownTimer? = null
    private var currentPackage: String? = null

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return

        val packageName = event.packageName?.toString() ?: return
        if (packageName == currentPackage) return

        currentPackage = packageName

        when (AppDataHolder.getOverride(packageName)) {
            AppDataHolder.OverrideState.NEVER_STOP -> {
                cancelTimer()
                return
            }

            AppDataHolder.OverrideState.ALWAYS_BLOCK -> {
                blockApp("This app is always blocked.")
                return
            }

            AppDataHolder.OverrideState.NONE -> {
                val allowedTime = AppDataHolder.getAppTime(packageName) ?: return

                if (AppDataHolder.isInCooldown(packageName)) {
                    blockApp("Please come back later. You're on a 1-hour cooldown.")
                    return
                }

                startTimer(packageName, allowedTime)
            }
        }
    }

    private fun startTimer(packageName: String, minutes: Int) {
        cancelTimer()

        val duration = minutes * 60 * 1000L

        timer = object : CountDownTimer(duration, 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                AppDataHolder.recordUsage(packageName)
                blockApp("You've reached your limit. Come back in 1 hour.")
            }
        }.start()
    }

    private fun cancelTimer() {
        timer?.cancel()
        timer = null
    }

    private fun blockApp(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()

        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }

    override fun onInterrupt() {
        cancelTimer()
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        serviceInfo = AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
            packageNames = null
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            notificationTimeout = 100
        }
    }
}

