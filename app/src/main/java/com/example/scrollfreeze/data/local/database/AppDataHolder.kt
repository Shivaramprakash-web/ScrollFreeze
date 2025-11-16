package com.example.scrollfreeze.data.local.database

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.scrollfreeze.R
import com.example.scrollfreeze.data.model.AppUsageInfo

object AppDataHolder {
    private val timeMap = mutableMapOf<String, Int>()
    private val lastUsedMap = mutableMapOf<String, Long>()
    private val overrideMap = mutableMapOf<String, OverrideState>()

    enum class OverrideState {
        NEVER_STOP, ALWAYS_BLOCK, NONE
    }

    fun setAppTime(packageName: String, minutes: Int) {
        timeMap[packageName] = minutes
        overrideMap[packageName] = OverrideState.NONE
    }

    fun getAppTime(packageName: String): Int? = timeMap[packageName]

    fun recordUsage(packageName: String) {
        lastUsedMap[packageName] = System.currentTimeMillis()
    }

    fun isInCooldown(packageName: String): Boolean {
        val lastUsed = lastUsedMap[packageName] ?: return false
        val oneHourMillis = 60 * 60 * 1000L
        return System.currentTimeMillis() - lastUsed < oneHourMillis
    }

    fun getRemainingCooldown(packageName: String): Long {
        val lastUsed = lastUsedMap[packageName] ?: return 0
        val elapsed = System.currentTimeMillis() - lastUsed
        val oneHourMillis = 60 * 60 * 1000L
        return (oneHourMillis - elapsed).coerceAtLeast(0)
    }

    fun setOverride(packageName: String, state: OverrideState) {
        overrideMap[packageName] = state
    }

    fun getOverride(packageName: String): OverrideState {
        return overrideMap[packageName] ?: OverrideState.NONE
    }
}

class AppUsageAdapter(private val items: List<AppUsageInfo>) :
    RecyclerView.Adapter<AppUsageAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val appName: TextView = view.findViewById(R.id.appName)
        val usageTime: TextView = view.findViewById(R.id.usageTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.usage_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.appName.text = item.appName
        holder.usageTime.text = "${item.minutesUsed} min"
    }

    override fun getItemCount(): Int = items.size
}
