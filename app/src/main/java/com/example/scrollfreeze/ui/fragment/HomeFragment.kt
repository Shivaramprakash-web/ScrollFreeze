package com.example.scrollfreeze.ui.fragment

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.scrollfreeze.R
import com.example.scrollfreeze.data.model.AppCheckResult
import com.example.scrollfreeze.databinding.FragmentHomeBinding
import com.example.scrollfreeze.ui.adapter.AppCheckAdapter
import com.example.scrollfreeze.utils.ScrollFreezeAccessibilityService


class HomeFragment : Fragment() {

    private lateinit var bindingHome: FragmentHomeBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindingHome = FragmentHomeBinding.inflate(inflater, container, false)

        requireActivity().window.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.colorPrimary)
        requireActivity().window.navigationBarColor =
            ContextCompat.getColor(requireContext(), R.color.colorPrimary)

        val recyclerView = bindingHome.root.findViewById<RecyclerView>(R.id.listview)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val appsToCheck = listOf(
            "YouTube (Shorts)" to "com.google.android.youtube",
            "WhatsApp" to "com.whatsapp",
            "LinkedIn" to "com.linkedin.android",
            "Instagram" to "com.instagram.android",
            "Reddit" to "com.reddit.frontpage",
            "Snapchat" to "com.snapchat.android",
            "TikTok" to "com.zhiliaoapp.musically",
            "X (Twitter)" to "com.twitter.android",
            "Opera Browser" to "com.opera.browser",
            "Google Chrome" to "com.android.chrome",
            "Hotstar" to "com.hotstar.android"
        )

        val results = appsToCheck.map { (name, pkg) ->
            AppCheckResult(name, pkg, isAppInstalled(pkg))
        }

        recyclerView.adapter = AppCheckAdapter(results)

        checkAccessibilityService()
        checkUsageAccessPermission()

        /*bindingHome.button.setOnClickListener {
            val intent = Intent(requireContext(), UsageDashboardActivity::class.java)
            startActivity(intent)
        }*/

        return bindingHome.root
    }

    private fun isAppInstalled(packageName: String): Boolean {
        return try {
            requireContext().packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    private fun checkAccessibilityService() {
        val serviceId = "${requireContext().packageName}/${ScrollFreezeAccessibilityService::class.java.name}"

        val enabledServices = Settings.Secure.getString(
            requireContext().contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )

        val isEnabled = enabledServices?.split(':')?.contains(serviceId) == true

        if (!isEnabled) {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            startActivity(intent)
            Toast.makeText(
                requireContext(),
                "Permission", Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun checkUsageAccessPermission() {
        val appOps = requireContext().getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            appOps.unsafeCheckOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), requireContext().packageName
            )
        } else {
            appOps.checkOpNoThrow(
                AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), requireContext().packageName
            )
        }

        if (mode != AppOpsManager.MODE_ALLOWED) {
            val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
            startActivity(intent)
            Toast.makeText(requireContext(), "Please enable Usage Access for ScrollFreeze", Toast.LENGTH_LONG).show()
        }
    }
}
