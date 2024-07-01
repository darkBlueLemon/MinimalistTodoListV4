package com.darkblue.minimalisttodolistv4.presentation.viewmodel

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
        }
    }
}
