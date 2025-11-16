package com.example.scrollfreeze.ui.components

import android.app.AlertDialog
import android.content.Context
import com.example.scrollfreeze.data.local.database.AppDataHolder
import com.example.scrollfreeze.data.model.AppCheckResult

fun showTimeDialog(context: Context, item: AppCheckResult, onTimeSelected: () -> Unit) {
    val options = (1..10).map { "$it min" } + listOf("NEVER", "ALWAYS")

    AlertDialog.Builder(context)
        .setTitle("Set Time Limit")
        .setItems(options.toTypedArray()) { _, which ->
            when {
                which < 10 -> {
                    item.selectedTime = which + 1
                    AppDataHolder.setAppTime(item.packageName, item.selectedTime!!)
                }
                options[which] == "NEVER" -> {
                    AppDataHolder.setOverride(item.packageName, AppDataHolder.OverrideState.NEVER_STOP)
                    item.selectedTime = null
                }
                options[which] == "ALWAYS" -> {
                    AppDataHolder.setOverride(item.packageName, AppDataHolder.OverrideState.ALWAYS_BLOCK)
                    item.selectedTime = null
                }
            }
            onTimeSelected()
        }
        .setNegativeButton("Cancel", null)
        .show()
}

