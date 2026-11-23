package com.example.ui.viewmodel

import android.Manifest
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.BatteryRepository
import com.example.data.UserPreferences
import com.example.data.UserPreferencesRepository
import com.example.model.BatteryInfo
import com.example.service.BatteryMonitorService
import com.example.service.BatteryNotificationHelper
import com.example.ui.theme.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class BatteryViewModel(application: Application) : AndroidViewModel(application) {

    private val context: Context get() = getApplication<Application>().applicationContext
    private val batteryRepository = BatteryRepository(context)
    private val userPreferencesRepository = UserPreferencesRepository(context)

    val batteryInfo: StateFlow<BatteryInfo> = batteryRepository.getBatteryInfoFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = batteryRepository.getCurrentBatteryInfo()
        )

    val userPreferences: StateFlow<UserPreferences> = userPreferencesRepository.userPreferencesFlow
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferences()
        )

    private val _hasNotificationPermission = MutableStateFlow(checkNotificationPermission())
    val hasNotificationPermission: StateFlow<Boolean> = _hasNotificationPermission.asStateFlow()

    private val _isChannelEnabled = MutableStateFlow(checkChannelEnabled())
    val isChannelEnabled: StateFlow<Boolean> = _isChannelEnabled.asStateFlow()

    private val _isServiceActive = MutableStateFlow(BatteryMonitorService.isRunning)
    val isServiceActive: StateFlow<Boolean> = _isServiceActive.asStateFlow()

    init {
        BatteryNotificationHelper.createNotificationChannel(context)
        refreshNotificationStatus()
    }

    private fun checkNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    private fun checkChannelEnabled(): Boolean {
        return BatteryNotificationHelper.areNotificationsEnabled(context)
    }

    fun refreshNotificationStatus() {
        _hasNotificationPermission.value = checkNotificationPermission()
        _isChannelEnabled.value = checkChannelEnabled()
        _isServiceActive.value = BatteryMonitorService.isRunning
    }

    fun onNotificationPermissionResult(granted: Boolean) {
        _hasNotificationPermission.value = granted
        if (granted && userPreferences.value.isLockscreenNotificationRequested) {
            startBatteryService()
        }
    }

    fun setLockscreenNotificationRequested(enabled: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.setLockscreenNotificationRequested(enabled)
            if (enabled) {
                if (checkNotificationPermission()) {
                    startBatteryService()
                }
            } else {
                stopBatteryService()
            }
            refreshNotificationStatus()
        }
    }

    fun setThemeMode(themeMode: ThemeMode) {
        viewModelScope.launch {
            userPreferencesRepository.setThemeMode(themeMode)
        }
    }

    private fun startBatteryService() {
        BatteryMonitorService.startService(context)
        _isServiceActive.value = true
    }

    private fun stopBatteryService() {
        BatteryMonitorService.stopService(context)
        _isServiceActive.value = false
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(BatteryViewModel::class.java)) {
                return BatteryViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
