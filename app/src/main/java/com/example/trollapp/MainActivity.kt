package com.example.trollapp

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    companion object {
        private const val TROLL_INTERVAL_MS = 11_000L
    }

    private val handler = Handler(Looper.getMainLooper())
    private var isRunning = false
    private lateinit var statusText: TextView
    private lateinit var startButton: Button
    private lateinit var stopButton: Button

    private val trollRunnable = object : Runnable {
        override fun run() {
            if (isRunning) {
                openRandomApp()
                handler.postDelayed(this, TROLL_INTERVAL_MS)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statusText = findViewById(R.id.statusText)
        startButton = findViewById(R.id.startButton)
        stopButton = findViewById(R.id.stopButton)

        stopButton.isEnabled = false

        startButton.setOnClickListener {
            startTrolling()
        }

        stopButton.setOnClickListener {
            stopTrolling()
        }
    }

    private fun startTrolling() {
        isRunning = true
        startButton.isEnabled = false
        stopButton.isEnabled = true
        statusText.text = getString(R.string.status_running)
        handler.post(trollRunnable)
    }

    private fun stopTrolling() {
        isRunning = false
        handler.removeCallbacks(trollRunnable)
        startButton.isEnabled = true
        stopButton.isEnabled = false
        statusText.text = getString(R.string.status_stopped)
    }

    private fun openRandomApp() {
        val pm: PackageManager = packageManager
        val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
            .filter { pm.getLaunchIntentForPackage(it.packageName) != null }
            .filter { it.packageName != packageName }

        if (packages.isNotEmpty()) {
            val randomApp = packages.random()
            val launchIntent = pm.getLaunchIntentForPackage(randomApp.packageName)
            launchIntent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            launchIntent?.let { startActivity(it) }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(trollRunnable)
    }
}
