package com.darkblue.minimalisttodolistv4.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.darkblue.minimalisttodolistv4.data.model.ClockType
import com.darkblue.minimalisttodolistv4.data.preferences.AppPreferences
import com.darkblue.minimalisttodolistv4.data.model.ThemeType
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PreferencesViewModel(private val appPreferences: AppPreferences) : ViewModel() {

    val theme: StateFlow<ThemeType> = appPreferences.theme
        .map { themeString ->
            ThemeType.fromDisplayName(themeString)
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, ThemeType.DARK)

    val clockType: StateFlow<ClockType> = appPreferences.clockType
        .map { clockTypeString ->
            ClockType.fromDisplayName(clockTypeString)
        }
        .stateIn(viewModelScope, SharingStarted.Eagerly, ClockType.TWELVE_HOUR)

    fun saveTheme(themeType: ThemeType) {
        viewModelScope.launch {
            appPreferences.saveTheme(themeType.toDisplayString())
        }
    }

    fun saveClockType(clockType: ClockType) {
        viewModelScope.launch {
            appPreferences.saveClockType(clockType.toDisplayString())
        }
    }
}

class PreferencesViewModelFactory(private val appPreferences: AppPreferences) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PreferencesViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PreferencesViewModel(appPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
