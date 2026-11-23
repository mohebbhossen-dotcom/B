package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.ui.theme.BatteryChargingCyan
import com.example.ui.theme.BatteryOrange
import com.example.ui.theme.VazirmatnFontFamily

@Composable
fun SamsungOneUiGuideCard(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("samsung_guide_card"),
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
            // Header Row (Clickable to collapse/expand)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(MaterialTheme.shapes.medium)
                    .clickable { isExpanded = !isExpanded }
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(
                                color = BatteryChargingCyan.copy(alpha = 0.15f),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhoneAndroid,
                            contentDescription = null,
                            tint = BatteryChargingCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "راهنمای صفحه قفل سامسونگ (One UI)",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontFamily = VazirmatnFontFamily,
                                fontWeight = FontWeight.Bold
                            ),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isExpanded) "برای بستن لمس کنید" else "تنظیمات One UI و شفافیت فنی (لمس کنید)",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = VazirmatnFontFamily
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(onClick = { isExpanded = !isExpanded }) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "بستن" else "باز کردن",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 14.dp)) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(14.dp))

                    // Step 1: Lock screen notifications in One UI
                    GuideStep(
                        stepNumber = "۱",
                        title = "فعال‌سازی نمایش محتوای اعلان در صفحه قفل سامسونگ",
                        description = "در گوشی‌های سامسونگ وارد مسیر زیر شوید:\n«تنظیمات (Settings) > صفحه قفل و AOD (Lock screen) > اعلان‌ها (Notifications)»\nسپس حالت را از «صرفاً نمادها (Icons only)» به «نمایش جزئیات (Details)» تغییر دهید تا درصد باتری خوانا باشد."
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Step 2: Battery optimization in Samsung
                    GuideStep(
                        stepNumber = "۲",
                        title = "جلوگیری از توقف پس‌زمینه در رابط کاربری One UI",
                        description = "سامسونگ برنامه‌های غیرفعال را پس از مدتی به حالت خواب عمیق (Deep sleep) می‌برد. برای فعالیت مداوم اعلان:\n«تنظیمات > باتری > محدودیت‌های پس‌زمینه > برنامه‌هایی که هرگز به خواب نمی‌روند (Never sleeping apps)» و این برنامه را اضافه کنید."
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Step 3: Honest Android Limitations
                    GuideStep(
                        stepNumber = "۳",
                        title = "توضیحات و شفافیت فنی (استاندارد رسمی اندروید)",
                        description = "• این برنامه درصد را از طریق اعلان استاندارد اندروید نمایش می‌دهد و به قوانین امنیتی سیستم احترام می‌گذارد.\n• هیچ برنامه شخص ثالثی در اندروید استاندارد اجازه ندارد متن دلخواه بزرگ را مستقیماً روی والپیپر یا ساعت Always On Display سامسونگ تزریق کند.\n• در اندروید ۱۴ و بالاتر، کاربر می‌تواند حتی اعلان‌های مداوم را حذف کند.\n• در صورت بستن کامل اجباری (Force Stop)، سیستم اجرای برنامه را متوقف می‌کند و تا باز شدن مجدد توسط کاربر، هیچ کدی اجرا نمی‌شود."
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Button to open Device Battery / App Info Settings
                    OutlinedButton(
                        onClick = { openAppDetails(context) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = MaterialTheme.shapes.medium
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "تنظیمات دسترسی و باتری این برنامه (App Info)",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontFamily = VazirmatnFontFamily
                            )
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GuideStep(
    stepNumber: String,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
            modifier = Modifier.size(26.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = stepNumber,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontFamily = VazirmatnFontFamily,
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontFamily = VazirmatnFontFamily,
                    fontWeight = FontWeight.Bold
                ),
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = VazirmatnFontFamily,
                    lineHeight = MaterialTheme.typography.bodySmall.lineHeight * 1.3
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun openAppDetails(context: Context) {
    try {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        // Ignore
    }
}
