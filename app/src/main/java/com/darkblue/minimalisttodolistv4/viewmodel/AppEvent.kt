package com.darkblue.minimalisttodolistv4.viewmodel

sealed interface AppEvent {
    data object ShowMenuDialog : AppEvent
    data object HideMenuDialog : AppEvent

    data object ShowHistoryDialog : AppEvent
    data object HideHistoryDialog : AppEvent

    data object ShowFontSettingsDialog : AppEvent
    data object HideFontSettingsDialog : AppEvent

    data object ShowScheduleExactAlarmPermissionDialog : AppEvent
    data object HideScheduleExactAlarmPermissionDialog : AppEvent

    data object ShowTutorialDialog : AppEvent
    data object HideTutorialDialog : AppEvent

    data object ShowScheduleExactAlarmPermissionIntent : AppEvent
    data object CheckNotificationPermissions : AppEvent
    data object IncrementPostNotificationDenialCount : AppEvent
}