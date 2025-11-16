package com.example.scrollfreeze.data.model

data class AppCheckResult(
    val appName: String,
    val packageName: String,
    val isInstalled: Boolean,
    var selectedTime: Int? = null,
)