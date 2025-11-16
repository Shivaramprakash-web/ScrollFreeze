package com.example.scrollfreeze.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.scrollfreeze.R
import com.example.scrollfreeze.data.local.database.AppDataHolder
import com.example.scrollfreeze.data.model.AppCheckResult
import com.example.scrollfreeze.ui.components.showTimeDialog

class AppCheckAdapter(private val items: List<AppCheckResult>) :
    RecyclerView.Adapter<AppCheckAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val appNameTextView: TextView = view.findViewById(R.id.titleItem)
        val statusTextView: TextView = view.findViewById(R.id.statusItem)
        val appIconImageView: ImageView = view.findViewById(R.id.imgViewItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val context = holder.statusTextView.context

        holder.appNameTextView.text = item.appName

        val iconResId = when (item.packageName) {
            "com.whatsapp" -> R.drawable.ic_whatsapp
            "com.instagram.android" -> R.drawable.ic_instagram
            "com.linkedin.android" -> R.drawable.ic_linkedin
            "com.google.android.youtube" -> R.drawable.ic_youtube
            "com.reddit.frontpage" -> R.drawable.ic_reddit
            "com.snapchat.android" -> R.drawable.ic_snapchat
            "com.zhiliaoapp.musically" -> R.drawable.ic_tiktok
            "com.twitter.android" -> R.drawable.ic_x_twitter
            "com.opera.browser" -> R.drawable.ic_opera
            "com.android.chrome" -> R.drawable.ic_chrome
            else -> R.drawable.chart
        }
        holder.appIconImageView.setImageResource(iconResId)

        // Set status based on app check results
        holder.statusTextView.text = when {
            !item.isInstalled -> "Not Installed"

            AppDataHolder.getOverride(item.packageName) == AppDataHolder.OverrideState.NEVER_STOP ->
                "Unlimited"

            AppDataHolder.getOverride(item.packageName) == AppDataHolder.OverrideState.ALWAYS_BLOCK ->
                "Blocked"

            AppDataHolder.isInCooldown(item.packageName) -> {
                val remaining = AppDataHolder.getRemainingCooldown(item.packageName) / 60000
                "Cooldown: $remaining min"
            }

            item.selectedTime != null -> "${item.selectedTime} min"

            else -> "Installed"
        }

        // Allow time setting via click
        holder.statusTextView.setOnClickListener {
            if (!item.isInstalled) return@setOnClickListener
            showTimeDialog(context, item) {
                notifyItemChanged(position)
            }
        }
    }

    override fun getItemCount(): Int = items.size
}
