package com.example.ui.screens

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.R
import com.example.ui.components.BatteryDetailsCard
import com.example.ui.components.CircularBatteryGauge
import com.example.ui.components.LockscreenNotificationCard
import com.example.ui.components.SamsungOneUiGuideCard
import com.example.ui.components.ThemeSelectorCard
import com.example.ui.theme.BatteryGreen
import com.example.ui.theme.VazirmatnFontFamily
import com.example.ui.viewmodel.BatteryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BatteryMainScreen(
    viewModel: BatteryViewModel,
    modifier: Modifier = Modifier
) {
    val batteryInfo by viewModel.batteryInfo.collectAsState()
    val userPreferences by viewModel.userPreferences.collectAsState()
    val hasNotificationPermission by viewModel.hasNotificationPermission.collectAsState()
    val isChannelEnabled by viewModel.isChannelEnabled.collectAsState()
    val isServiceActive by viewModel.isServiceActive.collectAsState()

    // Permission launcher for Android 13+ (Tiramisu)
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.onNotificationPermissionResult(isGranted)
    }

    // Refresh notification status when activity resumes
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.refreshNotificationStatus()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.BatteryChargingFull,
                            contentDescription = null,
                            tint = BatteryGreen,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = stringResource(R.string.app_name),
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontFamily = VazirmatnFontFamily,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .widthIn(max = 640.dp)
                    .testTag("battery_main_scroll_list"),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 1. Center Circular Battery Gauge
                item {
                    CircularBatteryGauge(
                        batteryInfo = batteryInfo,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                    )
                }

                // 2. Lockscreen Notification Card
                item {
                    LockscreenNotificationCard(
                        isRequestedByUser = userPreferences.isLockscreenNotificationRequested,
                        hasNotificationPermission = hasNotificationPermission,
                        isChannelEnabled = isChannelEnabled,
                        isServiceActive = isServiceActive,
                        onToggleRequested = { requested ->
                            viewModel.setLockscreenNotificationRequested(requested)
                        },
                        onRequestPermission = {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                        }
                    )
                }

                // 3. Technical Battery Details Card
                item {
                    BatteryDetailsCard(
                        batteryInfo = batteryInfo
                    )
                }

                // 4. Samsung One UI Guide Card
                item {
                    SamsungOneUiGuideCard()
                }

                // 5. AMOLED Theme Selector Card
                item {
                    ThemeSelectorCard(
                        currentThemeMode = userPreferences.themeMode,
                        onThemeSelected = { newTheme ->
                            viewModel.setThemeMode(newTheme)
                        }
                    )
                }

                // Footer spacer for edge-to-edge breathing room
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
