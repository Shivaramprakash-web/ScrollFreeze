package com.example.scrollfreeze.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.scrollfreeze.R
import com.example.scrollfreeze.databinding.ActivitySplashScreenBinding

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorPrimary)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.colorPrimary)

        Handler(Looper.getMainLooper()).postDelayed({
            initialiseAction()
        }, 5000)
    }

    private fun initialiseAction() {

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}