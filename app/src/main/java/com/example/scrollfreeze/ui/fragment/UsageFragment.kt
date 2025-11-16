package com.example.scrollfreeze.ui.fragment

import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.scrollfreeze.R
import com.example.scrollfreeze.data.local.database.AppUsageAdapter
import com.example.scrollfreeze.data.model.AppUsageInfo
import com.example.scrollfreeze.databinding.FragmentUsageBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate

class UsageFragment : Fragment() {

    private lateinit var bindingUsage: FragmentUsageBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindingUsage = FragmentUsageBinding.inflate(inflater, container, false)
        return bindingUsage.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().window.statusBarColor =
            ContextCompat.getColor(requireContext(), R.color.colorPrimary)

        bindingUsage.usageRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        val usageList = getAppUsageStats().filter { it.minutesUsed > 0 }
        bindingUsage.usageRecyclerView.adapter = AppUsageAdapter(usageList)

        setupBarChart(usageList)
    }

    private fun getAppUsageStats(): List<AppUsageInfo> {
        val usageStatsManager =
            requireContext().getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        val endTime = System.currentTimeMillis()
        val startTime = endTime - 1000 * 60 * 60 * 24 // last 24 hours

        val usageStats = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY, startTime, endTime
        )

        val packageManager = requireContext().packageManager

        return usageStats.filter { it.totalTimeInForeground > 0 }
            .sortedByDescending { it.totalTimeInForeground }
            .mapNotNull {
                try {
                    val appInfo = packageManager.getApplicationInfo(it.packageName, 0)
                    val appName = packageManager.getApplicationLabel(appInfo).toString()
                    AppUsageInfo(appName, it.packageName, it.totalTimeInForeground / 60000)
                } catch (e: PackageManager.NameNotFoundException) {
                    null
                }
            }
    }

    private fun setupBarChart(appUsageList: List<AppUsageInfo>) {
        val barChart = bindingUsage.usageBarChart

        val entries = appUsageList.mapIndexed { index, app ->
            BarEntry(index.toFloat(), app.minutesUsed.toFloat())
        }

        val dataSet = BarDataSet(entries, "App Usage (min)").apply {
            setColors(ColorTemplate.MATERIAL_COLORS, 255)
            valueTextColor = Color.BLACK
            valueTextSize = 14f
        }

        val barData = BarData(dataSet)
        barData.barWidth = 0.9f

        val xAxisLabels = appUsageList.map { it.appName.take(10) }

        val xAxis = barChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(xAxisLabels)
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(true)
        xAxis.granularity = 1f
        xAxis.labelRotationAngle = -45f
        xAxis.textSize = 12f

        barChart.axisLeft.axisMinimum = 0f
        barChart.axisRight.isEnabled = false
        barChart.description.isEnabled = false
        barChart.legend.isEnabled = true

        barChart.setFitBars(true)
        barChart.data = barData
        barChart.animateY(1000)
        barChart.invalidate()
    }
}

