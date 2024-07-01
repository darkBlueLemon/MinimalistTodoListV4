package com.darkblue.minimalisttodolistv4.presentation.viewmodel

sealed interface AppEvent {
    data object ShowMenuDialog : AppEvent
    data object HideMenuDialog : AppEvent

    data object ShowHistoryDialog : AppEvent
    data object HideHistoryDialog : AppEvent

    data object ShowScheduleExactAlarmPermissionDialog: AppEvent
    data object HideScheduleExactAlarmPermissionDialog : AppEvent

    data object ShowScheduleExactAlarmPermissionIntent: AppEvent
    data object CheckNotificationPermissions: AppEvent
}