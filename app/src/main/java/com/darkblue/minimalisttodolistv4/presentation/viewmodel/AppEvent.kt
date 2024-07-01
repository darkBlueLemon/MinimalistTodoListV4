package com.darkblue.minimalisttodolistv4.presentation.viewmodel

sealed interface AppEvent {
    object ShowMenuDialog : AppEvent
    object HideMenuDialog : AppEvent
    object ShowHistoryDialog : AppEvent
    object HideHistoryDialog : AppEvent
}