package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.service.BatteryNotificationHelper
import com.example.ui.theme.BatteryGreen
import com.example.ui.theme.BatteryOrange
import com.example.ui.theme.BatteryRed
import com.example.ui.theme.VazirmatnFontFamily

@Composable
fun LockscreenNotificationCard(
    isRequestedByUser: Boolean,
    hasNotificationPermission: Boolean,
    isChannelEnabled: Boolean,
    isServiceActive: Boolean,
    onToggleRequested: (Boolean) -> Unit,
    onRequestPermission: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Notification is truly active ONLY when user requested it AND permission is granted AND channel is not blocked
    val isTrulyActive = isRequestedByUser && hasNotificationPermission && isChannelEnabled && isServiceActive

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("lockscreen_notification_card"),
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
            // Main Switch Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(
                                color = if (isTrulyActive) BatteryGreen.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = if (isTrulyActive) BatteryGreen else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "نمایش درصد باتری روی صفحه قفل",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = VazirmatnFontFamily,
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "از طریق اعلان ماندگار (Ongoing) با دسترسی عمومی",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = VazirmatnFontFamily
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Switch(
                    checked = isRequestedByUser,
                    onCheckedChange = { checked ->
                        if (checked && !hasNotificationPermission) {
                            onRequestPermission()
                        }
                        onToggleRequested(checked)
                    },
                    modifier = Modifier.testTag("lockscreen_notification_switch"),
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = BatteryGreen
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            Spacer(modifier = Modifier.height(14.dp))

            // Real System Status Indicators
            Text(
                text = "وضعیت واقعی قابلیت و پیش‌نیازها در سیستم:",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontFamily = VazirmatnFontFamily,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(10.dp))

            StatusItem(
                label = "درخواست کاربر (تنظیمات برنامه):",
                status = if (isRequestedByUser) "فعال" else "غیرفعال",
                isOk = isRequestedByUser
            )

            StatusItem(
                label = "مجوز اعلان‌های سیستم (Android):",
                status = if (hasNotificationPermission) "اعطا شده" else "نیاز به اجازه کاربر",
                isOk = hasNotificationPermission
            )

            StatusItem(
                label = "وضعیت کانال اعلان در تنظیمات گوشی:",
                status = if (isChannelEnabled) "مجاز و فعال" else "مسدود شده توسط کاربر",
                isOk = isChannelEnabled
            )

            StatusItem(
                label = "سرویس پایش پس‌زمینه (Foreground Service):",
                status = if (isServiceActive) "در حال اجرا" else "متوقف",
                isOk = isServiceActive
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Overall Summary Banner
            if (isTrulyActive) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            BatteryGreen.copy(alpha = 0.12f),
                            shape = MaterialTheme.shapes.small
                        )
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = BatteryGreen,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "اعلان فعال است و با تغییر درصد باتری به‌روزرسانی می‌شود.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = VazirmatnFontFamily,
                            fontWeight = FontWeight.Medium
                        ),
                        color = BatteryGreen
                    )
                }
            } else if (isRequestedByUser && !hasNotificationPermission) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            BatteryOrange.copy(alpha = 0.12f),
                            shape = MaterialTheme.shapes.small
                        )
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = null,
                        tint = BatteryOrange,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "سوییچ روشن است اما مجوز اعلان داده نشده است.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = VazirmatnFontFamily,
                            fontWeight = FontWeight.Medium
                        ),
                        color = BatteryOrange
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action button to open Notification Settings
            OutlinedButton(
                onClick = { openAppNotificationSettings(context) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("open_notification_settings_button"),
                shape = MaterialTheme.shapes.medium
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "باز کردن تنظیمات اعلان این برنامه در سامسونگ",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontFamily = VazirmatnFontFamily
                    )
                )
            }
        }
    }
}

@Composable
private fun StatusItem(
    label: String,
    status: String,
    isOk: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(
                fontFamily = VazirmatnFontFamily
            ),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (isOk) Icons.Default.Check else Icons.Default.Close,
                contentDescription = null,
                tint = if (isOk) BatteryGreen else BatteryRed,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = status,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = VazirmatnFontFamily,
                    fontWeight = FontWeight.SemiBold
                ),
                color = if (isOk) BatteryGreen else BatteryRed
            )
        }
    }
}

private fun openAppNotificationSettings(context: Context) {
    try {
        val intent = Intent().apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            } else {
                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                data = Uri.fromParts("package", context.packageName, null)
            }
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        // Fallback to app details
        val fallbackIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(fallbackIntent)
    }
}
