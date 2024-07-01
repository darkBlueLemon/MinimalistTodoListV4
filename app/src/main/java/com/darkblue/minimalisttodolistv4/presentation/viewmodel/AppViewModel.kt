package com.darkblue.minimalisttodolistv4.presentation.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class AppViewModel : ViewModel() {
    private val _state = MutableStateFlow(AppState())
    val state: StateFlow<AppState> = _state

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
        }
    }

//    // Permission
//    var showPermissionDialog by mutableStateOf(false)
//        private set
//
//    fun showPermissionDialog() {
//        showPermissionDialog = true
//    }
//
//    fun hidePermissionDialog() {
//        showPermissionDialog = false
//    }
}
