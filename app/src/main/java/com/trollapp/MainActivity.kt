package com.trollapp

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private val handler = Handler(Looper.getMainLooper())
    private var isRunning = false

    private val trollRunnable = object : Runnable {
        override fun run() {
            if (isRunning) {
                openRandomApp()
                handler.postDelayed(this, 11_000L)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val btnStart = findViewById<Button>(R.id.btnStart)
        val btnStop = findViewById<Button>(R.id.btnStop)

        btnStart.setOnClickListener {
            if (!isRunning) {
                isRunning = true
                Toast.makeText(this, getString(R.string.troll_started), Toast.LENGTH_SHORT).show()
                handler.post(trollRunnable)
            }
        }

        btnStop.setOnClickListener {
            if (isRunning) {
                isRunning = false
                handler.removeCallbacks(trollRunnable)
                Toast.makeText(this, getString(R.string.troll_stopped), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openRandomApp() {
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }
        val apps: List<ResolveInfo> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.queryIntentActivities(intent, PackageManager.ResolveInfoFlags.of(0L))
        } else {
            @Suppress("DEPRECATION")
            packageManager.queryIntentActivities(intent, 0)
        }
        if (apps.isEmpty()) return

        val randomApp = apps.random()
        val launchIntent = packageManager.getLaunchIntentForPackage(
            randomApp.activityInfo.packageName
        ) ?: return

        launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(launchIntent)
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        handler.removeCallbacks(trollRunnable)
    }
}
