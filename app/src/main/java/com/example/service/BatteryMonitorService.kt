package com.example.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.ServiceCompat
import com.example.data.BatteryRepository
import com.example.model.BatteryInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class BatteryMonitorService : Service() {

    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())
    private lateinit var batteryRepository: BatteryRepository
    private var isMonitoringStarted = false

    companion object {
        const val ACTION_START = "com.example.service.ACTION_START"
        const val ACTION_STOP = "com.example.service.ACTION_STOP"
        var isRunning: Boolean = false
            private set

        fun startService(context: Context) {
            val intent = Intent(context, BatteryMonitorService::class.java).apply {
                action = ACTION_START
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stopService(context: Context) {
            val intent = Intent(context, BatteryMonitorService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        batteryRepository = BatteryRepository(applicationContext)
        BatteryNotificationHelper.createNotificationChannel(applicationContext)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            isRunning = false
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
            return START_NOT_STICKY
        }

        isRunning = true
        startForegroundNotification()

        if (!isMonitoringStarted) {
            isMonitoringStarted = true
            serviceScope.launch {
                batteryRepository.getBatteryInfoFlow().collectLatest { info ->
                    updateNotification(info)
                }
            }
        }

        return START_STICKY
    }

    private fun startForegroundNotification() {
        val initialInfo = batteryRepository.getCurrentBatteryInfo()
        val notification = BatteryNotificationHelper.buildBatteryNotification(this, initialInfo)

        val foregroundServiceType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
        } else {
            0
        }

        ServiceCompat.startForeground(
            this,
            BatteryNotificationHelper.NOTIFICATION_ID,
            notification,
            foregroundServiceType
        )
    }

    private fun updateNotification(batteryInfo: BatteryInfo) {
        if (!isRunning) return
        val notification = BatteryNotificationHelper.buildBatteryNotification(this, batteryInfo)
        try {
            val notificationManager = NotificationManagerCompat.from(this)
            notificationManager.notify(BatteryNotificationHelper.NOTIFICATION_ID, notification)
        } catch (e: SecurityException) {
            // Missing POST_NOTIFICATIONS on Android 13+
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        serviceScope.cancel()
    }
}
