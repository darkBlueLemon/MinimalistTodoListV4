package com.darkblue.minimalisttodolistv4.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.darkblue.minimalisttodolistv4.PermissionManager
import com.darkblue.minimalisttodolistv4.data.preferences.AppPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class AppViewModel: ViewModel() {
    private val _state = MutableStateFlow(AppState())
    val state: StateFlow<AppState> = _state
    private lateinit var permissionManager: PermissionManager

    fun setPermissionManager(permissionManager: PermissionManager) {
        this.permissionManager = permissionManager
    }

    fun onEvent(event: AppEvent) {
        when (event) {
            AppEvent.ShowMenuDialog -> {
                _state.update { it.copy(isMenuDialogVisible = true) }
            }
            AppEvent.HideMenuDialog -> {
                _state.update { it.copy(isMenuDialogVisible = false) }
            }
            AppEvent.ShowHistoryDialog -> {
                _state.update { it.copy(isHistoryDialogVisible = true, isMenuDialogVisible = false) }
            }
            AppEvent.HideHistoryDialog -> {
                _state.update { it.copy(isHistoryDialogVisible = false) }
            }

            AppEvent.ShowScheduleExactAlarmPermissionDialog -> {
                _state.update { it.copy(isScheduleExactAlarmPermissionDialogVisible = true) }
            }
            AppEvent.HideScheduleExactAlarmPermissionDialog -> {
                _state.update { it.copy(isScheduleExactAlarmPermissionDialogVisible = false) }
            }
            AppEvent.ShowScheduleExactAlarmPermissionIntent -> {
                permissionManager.requestScheduleExactAlarmPermission()
            }
            AppEvent.CheckNotificationPermissions -> {
                permissionManager.requestPermissions()
            }
        }
    }
}
