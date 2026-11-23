package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.BatteryInfo
import com.example.model.BatteryStatus
import com.example.model.PlugType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

    @Test
    fun `read app_name string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("درصد باتری", appName)
    }

    @Test
    fun `test persian digit conversion`() {
        assertEquals("۳۶", BatteryInfo.toPersianDigits(36))
        assertEquals("۱۰۰", BatteryInfo.toPersianDigits(100))
        assertEquals("۰", BatteryInfo.toPersianDigits(0))
    }

    @Test
    fun `test battery percentage single text formatting`() {
        val info = BatteryInfo(
            level = 75,
            status = BatteryStatus.CHARGING,
            plugType = PlugType.AC,
            temperatureCelsius = 31.5f,
            voltageVolts = 4.1f,
            health = com.example.model.BatteryHealth.GOOD,
            technology = "Li-ion"
        )
        assertEquals("۷۵٪", info.formattedPercentage())
        assertEquals("در حال شارژ", info.statusDescription())
        assertTrue(info.isValid)
    }
}
