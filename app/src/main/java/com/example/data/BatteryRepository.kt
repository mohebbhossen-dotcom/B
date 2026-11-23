package com.example.data

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import com.example.model.BatteryHealth
import com.example.model.BatteryInfo
import com.example.model.BatteryStatus
import com.example.model.PlugType
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlin.math.roundToInt

class BatteryRepository(private val context: Context) {

    /**
     * Reads current battery intent synchronously without waiting for a broadcast
     */
    fun getCurrentBatteryInfo(): BatteryInfo {
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatusIntent: Intent? = context.registerReceiver(null, filter)
        return parseBatteryIntent(batteryStatusIntent)
    }

    /**
     * Flow that yields the initial sticky battery state and updates on any system battery change
     */
    fun getBatteryInfoFlow(): Flow<BatteryInfo> = callbackFlow {
        // Emit initial sticky state
        trySend(getCurrentBatteryInfo())

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == Intent.ACTION_BATTERY_CHANGED) {
                    val info = parseBatteryIntent(intent)
                    trySend(info)
                }
            }
        }

        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        context.registerReceiver(receiver, filter)

        awaitClose {
            try {
                context.unregisterReceiver(receiver)
            } catch (e: Exception) {
                // Ignore if already unregistered
            }
        }
    }

    private fun parseBatteryIntent(intent: Intent?): BatteryInfo {
        if (intent == null) {
            return BatteryInfo.Unavailable
        }

        // Calculate Level and Scale
        val rawLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
        val rawScale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1)

        val percentage: Int? = if (rawLevel >= 0 && rawScale > 0) {
            val calc = ((rawLevel.toFloat() / rawScale.toFloat()) * 100f).roundToInt()
            calc.coerceIn(0, 100)
        } else {
            null
        }

        // Status
        val statusExtra = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
        val status = when (statusExtra) {
            BatteryManager.BATTERY_STATUS_CHARGING -> BatteryStatus.CHARGING
            BatteryManager.BATTERY_STATUS_DISCHARGING -> BatteryStatus.DISCHARGING
            BatteryManager.BATTERY_STATUS_FULL -> BatteryStatus.FULL
            BatteryManager.BATTERY_STATUS_NOT_CHARGING -> BatteryStatus.NOT_CHARGING
            else -> BatteryStatus.UNKNOWN
        }

        // Plugged
        val pluggedExtra = intent.getIntExtra(BatteryManager.EXTRA_PLUGGED, 0)
        val plugType = when {
            (pluggedExtra and BatteryManager.BATTERY_PLUGGED_AC) != 0 -> PlugType.AC
            (pluggedExtra and BatteryManager.BATTERY_PLUGGED_USB) != 0 -> PlugType.USB
            (pluggedExtra and BatteryManager.BATTERY_PLUGGED_WIRELESS) != 0 -> PlugType.WIRELESS
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                    (pluggedExtra and BatteryManager.BATTERY_PLUGGED_DOCK) != 0 -> PlugType.DOCK
            else -> PlugType.UNPLUGGED
        }

        // Temperature (tenths of degree Celsius -> °C)
        val tempRaw = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1)
        val temperatureCelsius = if (tempRaw > 0) tempRaw / 10.0f else null

        // Voltage (millivolts -> Volts)
        val voltRaw = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1)
        val voltageVolts = if (voltRaw > 0) {
            // Note: On some devices EXTRA_VOLTAGE is in mV (e.g. 4100 mV), on rare devices already in V
            if (voltRaw > 100) voltRaw / 1000.0f else voltRaw.toFloat()
        } else null

        // Health
        val healthExtra = intent.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_UNKNOWN)
        val health = when (healthExtra) {
            BatteryManager.BATTERY_HEALTH_GOOD -> BatteryHealth.GOOD
            BatteryManager.BATTERY_HEALTH_OVERHEAT -> BatteryHealth.OVERHEAT
            BatteryManager.BATTERY_HEALTH_DEAD -> BatteryHealth.DEAD
            BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE -> BatteryHealth.OVER_VOLTAGE
            BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE -> BatteryHealth.UNSPECIFIED_FAILURE
            BatteryManager.BATTERY_HEALTH_COLD -> BatteryHealth.COLD
            else -> BatteryHealth.UNKNOWN
        }

        // Technology
        val technology = intent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY)?.takeIf { it.isNotBlank() }

        return BatteryInfo(
            level = percentage,
            status = status,
            plugType = plugType,
            temperatureCelsius = temperatureCelsius,
            voltageVolts = voltageVolts,
            health = health,
            technology = technology,
            timestamp = System.currentTimeMillis()
        )
    }
}
