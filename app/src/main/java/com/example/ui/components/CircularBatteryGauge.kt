package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Power
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BatteryInfo
import com.example.model.BatteryStatus
import com.example.model.BatteryThresholds
import com.example.ui.theme.BatteryChargingCyan
import com.example.ui.theme.BatteryGreen
import com.example.ui.theme.BatteryNeutral
import com.example.ui.theme.BatteryOrange
import com.example.ui.theme.BatteryRed
import com.example.ui.theme.VazirmatnFontFamily

@Composable
fun CircularBatteryGauge(
    batteryInfo: BatteryInfo,
    modifier: Modifier = Modifier
) {
    val level = batteryInfo.level ?: 0
    val isValid = batteryInfo.isValid

    // Determine target gauge color based on thresholds & charging status
    val targetColor = when {
        !isValid -> BatteryNeutral
        batteryInfo.status == BatteryStatus.CHARGING -> BatteryChargingCyan
        level < BatteryThresholds.LOW_PERCENT -> BatteryRed
        level < BatteryThresholds.MEDIUM_PERCENT -> BatteryOrange
        else -> BatteryGreen
    }

    val animatedColor by animateColorAsState(
        targetValue = targetColor,
        animationSpec = tween(durationMillis = 600),
        label = "gaugeColor"
    )

    val targetSweep = if (isValid) (level / 100f) * 360f else 0f
    val animatedSweep by animateFloatAsState(
        targetValue = targetSweep,
        animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing),
        label = "gaugeSweep"
    )

    Column(
        modifier = modifier.testTag("battery_gauge_card"),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Outer box constraining size
        BoxWithConstraints(
            modifier = Modifier
                .size(240.dp)
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            val strokeWidthDp = 18.dp
            val innerDiameter = maxWidth - strokeWidthDp * 2

            // Canvas drawing background track and filled arc
            Canvas(modifier = Modifier.fillMaxSize()) {
                val strokePx = strokeWidthDp.toPx()
                val diameter = size.minDimension - strokePx
                val topLeft = Offset(
                    (size.width - diameter) / 2f,
                    (size.height - diameter) / 2f
                )
                val arcSize = Size(diameter, diameter)

                // Background track
                drawArc(
                    color = animatedColor.copy(alpha = 0.15f),
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokePx, cap = StrokeCap.Round)
                )

                if (isValid && animatedSweep > 0f) {
                    // Filled progress arc with subtle gradient
                    drawArc(
                        brush = Brush.sweepGradient(
                            colors = listOf(
                                animatedColor.copy(alpha = 0.85f),
                                animatedColor
                            )
                        ),
                        startAngle = -90f,
                        sweepAngle = animatedSweep,
                        useCenter = false,
                        topLeft = topLeft,
                        size = arcSize,
                        style = Stroke(width = strokePx, cap = StrokeCap.Round)
                    )
                }
            }

            // Center Text inside the ring
            Column(
                modifier = Modifier
                    .widthIn(max = innerDiameter)
                    .padding(horizontal = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (batteryInfo.status == BatteryStatus.CHARGING) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = "در حال شارژ",
                        tint = BatteryChargingCyan,
                        modifier = Modifier.size(26.dp)
                    )
                }

                // Adaptive font sizing: "۱۰۰٪" has 4 characters.
                // Dynamic sizing ensures it never clips or wraps regardless of device width.
                val baseFontSize = (innerDiameter.value * 0.28f).coerceIn(34f, 54f).sp

                Text(
                    text = batteryInfo.formattedPercentage(),
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontFamily = VazirmatnFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = baseFontSize,
                        lineHeight = baseFontSize,
                        textDirection = TextDirection.Ltr,
                        textAlign = TextAlign.Center
                    ),
                    color = if (isValid) animatedColor else MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    softWrap = false,
                    modifier = Modifier.testTag("battery_percentage_text")
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Status Badge separate from the number
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = animatedColor.copy(alpha = 0.14f),
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 14.dp, vertical = 8.dp)
                    .testTag("battery_status_text"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val statusIcon = when (batteryInfo.status) {
                    BatteryStatus.CHARGING -> Icons.Default.Bolt
                    BatteryStatus.FULL -> Icons.Default.CheckCircle
                    BatteryStatus.NOT_CHARGING -> Icons.Default.Power
                    BatteryStatus.DISCHARGING -> if (level < BatteryThresholds.LOW_PERCENT) Icons.Default.Warning else Icons.Default.CheckCircle
                    BatteryStatus.UNKNOWN -> Icons.Default.Warning
                }

                Icon(
                    imageVector = statusIcon,
                    contentDescription = null,
                    tint = animatedColor,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = if (isValid) batteryInfo.statusDescription() else "اطلاعات باتری در دسترس نیست",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = VazirmatnFontFamily,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = animatedColor
                )
            }
        }
    }
}
