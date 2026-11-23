package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.model.BatteryInfo
import com.example.ui.theme.VazirmatnFontFamily

@Composable
fun BatteryDetailsCard(
    batteryInfo: BatteryInfo,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("battery_details_card"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
        ),
        shape = MaterialTheme.shapes.large
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {
            Text(
                text = "مشخصات فنی سخت‌افزار باتری",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontFamily = VazirmatnFontFamily,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(14.dp))

            MetricRow(
                icon = Icons.Default.Power,
                label = "منبع تغذیه ورودی",
                value = batteryInfo.plugDescription(),
                testTag = "metric_plug"
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 10.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            )

            MetricRow(
                icon = Icons.Default.Thermostat,
                label = "دمای باتری",
                value = batteryInfo.formattedTemperature(),
                testTag = "metric_temperature"
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 10.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            )

            MetricRow(
                icon = Icons.Default.ElectricBolt,
                label = "ولتاژ باتری",
                value = batteryInfo.formattedVoltage(),
                testTag = "metric_voltage"
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 10.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            )

            MetricRow(
                icon = Icons.Default.Favorite,
                label = "سلامت باتری",
                value = batteryInfo.healthDescription(),
                testTag = "metric_health"
            )

            if (batteryInfo.technology != null) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 10.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                )

                MetricRow(
                    icon = Icons.Default.Memory,
                    label = "فناوری ساخت",
                    value = batteryInfo.technology,
                    testTag = "metric_technology"
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 10.dp),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
            )

            MetricRow(
                icon = Icons.Default.AccessTime,
                label = "آخرین دریافت داده از سیستم",
                value = batteryInfo.formattedUpdateTime(),
                testTag = "metric_timestamp"
            )
        }
    }
}

@Composable
private fun MetricRow(
    icon: ImageVector,
    label: String,
    value: String,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontFamily = VazirmatnFontFamily
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = VazirmatnFontFamily,
                fontWeight = FontWeight.SemiBold
            ),
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}
