package com.example.scrollfreeze.utils

import android.accessibilityservice.AccessibilityService
import android.content.Intent
import android.view.accessibility.AccessibilityEvent
import com.example.scrollfreeze.data.local.database.AppDataHolder

class AppAccessibilityService : AccessibilityService() {

    private val appTimers = mutableMapOf<String, Long>()

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event?.eventType != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) return

        val packageName = event.packageName?.toString() ?: return
        val currentTime = System.currentTimeMillis()

        val allowedTime = AppDataHolder.getAppTime(packageName) ?: return

        if (!appTimers.containsKey(packageName)) {
            appTimers[packageName] = currentTime + allowedTime * 60 * 1000
        } else if (currentTime >= (appTimers[packageName] ?: 0)) {
            exitApp()
        }
    }

    override fun onInterrupt() {}

    private fun exitApp() {
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
    }
}
