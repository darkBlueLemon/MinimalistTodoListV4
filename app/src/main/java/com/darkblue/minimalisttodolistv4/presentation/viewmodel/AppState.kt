package com.darkblue.minimalisttodolistv4.presentation.viewmodel

data class AppState(
    val isMenuDialogVisible: Boolean = false,
    val isHistoryDialogVisible: Boolean = false,
    val isScheduleExactAlarmPermissionDialogVisible: Boolean = false,
)