package com.example.llamada_sonar

import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.content.Context
import android.app.NotificationManager
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

class MainActivity : FlutterActivity() {

    private val CHANNEL = "com.example.llamada_sonar/call_service"

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler { call, result ->
            when (call.method) {
                "startCallService" -> {
                    startCallService()
                    result.success("Call service started")
                }
                "checkNotificationPolicyPermission" -> {
                    result.success(checkNotificationPolicyPermission())
                }
                "requestNotificationPolicyPermission" -> {
                    requestNotificationPolicyPermission()
                    result.success(null)
                }
                else -> result.notImplemented()
            }
        }
    }

    private fun startCallService() {
        val serviceIntent = Intent(this, CallService::class.java)
        startForegroundService(serviceIntent)
    }

    private fun checkNotificationPolicyPermission(): Boolean {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        return notificationManager.isNotificationPolicyAccessGranted
    }

    private fun requestNotificationPolicyPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }
}
