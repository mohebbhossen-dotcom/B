package com.example.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Battery status representation mapped directly from Android BatteryManager
 */
enum class BatteryStatus {
    CHARGING,
    DISCHARGING,
    FULL,
    NOT_CHARGING,
    UNKNOWN
}

enum class PlugType {
    AC,
    USB,
    WIRELESS,
    DOCK,
    UNPLUGGED
}

enum class BatteryHealth {
    GOOD,
    OVERHEAT,
    DEAD,
    OVER_VOLTAGE,
    UNSPECIFIED_FAILURE,
    COLD,
    UNKNOWN
}

object BatteryThresholds {
    const val LOW_PERCENT = 20
    const val MEDIUM_PERCENT = 50
}

data class BatteryInfo(
    val level: Int?, // 0..100 or null if invalid/unavailable
    val status: BatteryStatus,
    val plugType: PlugType,
    val temperatureCelsius: Float?,
    val voltageVolts: Float?,
    val health: BatteryHealth,
    val technology: String?,
    val timestamp: Long = System.currentTimeMillis()
) {
    val isValid: Boolean get() = level != null && level in 0..100
    val isCharging: Boolean get() = status == BatteryStatus.CHARGING

    /**
     * Converts an integer or number string to Persian digits (۰-۹)
     */
    companion object {
        fun toPersianDigits(input: String): String {
            val persianDigits = charArrayOf('۰', '۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹')
            val sb = java.lang.StringBuilder()
            for (char in input) {
                if (char in '0'..'9') {
                    sb.append(persianDigits[char - '0'])
                } else {
                    sb.append(char)
                }
            }
            return sb.toString()
        }

        fun toPersianDigits(number: Int): String {
            return toPersianDigits(number.toString())
        }

        val Unavailable = BatteryInfo(
            level = null,
            status = BatteryStatus.UNKNOWN,
            plugType = PlugType.UNPLUGGED,
            temperatureCelsius = null,
            voltageVolts = null,
            health = BatteryHealth.UNKNOWN,
            technology = null
        )
    }

    /**
     * Formats the battery percentage as a single string with Persian digits and percent sign (e.g. "۳۶٪")
     */
    fun formattedPercentage(): String {
        return if (level != null && level in 0..100) {
            "${toPersianDigits(level)}٪"
        } else {
            "نامشخص"
        }
    }

    fun statusDescription(): String {
        return when (status) {
            BatteryStatus.CHARGING -> "در حال شارژ"
            BatteryStatus.DISCHARGING -> "در حال استفاده از باتری"
            BatteryStatus.FULL -> "شارژ کامل"
            BatteryStatus.NOT_CHARGING -> "متصل به برق (عدم شارژ)"
            BatteryStatus.UNKNOWN -> "وضعیت نامشخص"
        }
    }

    fun plugDescription(): String {
        return when (plugType) {
            PlugType.AC -> "شارژر دیواری (AC)"
            PlugType.USB -> "درگاه یو‌اس‌بی (USB)"
            PlugType.WIRELESS -> "شارژر بی‌سیم"
            PlugType.DOCK -> "پایه شارژ (Dock)"
            PlugType.UNPLUGGED -> "جدا شده از شارژر"
        }
    }

    fun healthDescription(): String {
        return when (health) {
            BatteryHealth.GOOD -> "سالم و عادی"
            BatteryHealth.OVERHEAT -> "داغی بیش از حد"
            BatteryHealth.DEAD -> "خراب / تخلیه کامل"
            BatteryHealth.OVER_VOLTAGE -> "ولتاژ بیش از حد"
            BatteryHealth.UNSPECIFIED_FAILURE -> "خطای نامشخص سخت‌افزار"
            BatteryHealth.COLD -> "دمای بسیار پایین"
            BatteryHealth.UNKNOWN -> "نامشخص"
        }
    }

    fun formattedTemperature(): String {
        return if (temperatureCelsius != null) {
            val formatted = String.format(Locale.US, "%.1f", temperatureCelsius)
            "${toPersianDigits(formatted)} درجه سانتی‌گراد"
        } else {
            "نامشخص"
        }
    }

    fun formattedVoltage(): String {
        return if (voltageVolts != null) {
            val formatted = String.format(Locale.US, "%.2f", voltageVolts)
            "${toPersianDigits(formatted)} ولت"
        } else {
            "نامشخص"
        }
    }

    fun formattedUpdateTime(): String {
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.US)
        val timeStr = sdf.format(Date(timestamp))
        return toPersianDigits(timeStr)
    }
}
