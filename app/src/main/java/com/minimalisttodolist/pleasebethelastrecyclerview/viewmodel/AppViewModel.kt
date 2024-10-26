package com.minimalisttodolist.pleasebethelastrecyclerview.viewmodel

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.minimalisttodolist.pleasebethelastrecyclerview.data.preferences.AppPreferences
import com.minimalisttodolist.pleasebethelastrecyclerview.util.PermissionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AppViewModel(private val appPreferences: AppPreferences) : ViewModel() {
    private val _state = MutableStateFlow(AppState())
    val state: StateFlow<AppState> = _state

    private lateinit var permissionManager: PermissionManager

    fun setPermissionManager(permissionManager: PermissionManager) {
        this.permissionManager = permissionManager
    }

    fun onEvent(event: AppEvent) {
        when (event) {
            AppEvent.ShowMenuDialog -> updateState { copy(isMenuDialogVisible = true) }
            AppEvent.HideMenuDialog -> updateState { copy(isMenuDialogVisible = false) }

            AppEvent.ShowHistoryDialog -> updateState {
                copy(isHistoryDialogVisible = true, isMenuDialogVisible = false)
            }
            AppEvent.HideHistoryDialog -> updateState { copy(isHistoryDialogVisible = false) }

            AppEvent.ShowFontSettingsDialog -> updateState {
                copy(isFontSettingsDialogVisible = true, isMenuDialogVisible = false)
            }
            AppEvent.HideFontSettingsDialog -> updateState { copy(isFontSettingsDialogVisible = false) }

            AppEvent.ShowScheduleExactAlarmPermissionDialog -> updateState {
                copy(isScheduleExactAlarmPermissionDialogVisible = true)
            }
            AppEvent.HideScheduleExactAlarmPermissionDialog -> updateState {
                copy(isScheduleExactAlarmPermissionDialogVisible = false)
            }

            AppEvent.ShowPersonalizeDialog -> updateState { copy(isPersonalizeDialogVisible = true, isMenuDialogVisible = false) }
            AppEvent.HidePersonalizeDialog -> updateState { copy(isPersonalizeDialogVisible = false) }

            AppEvent.ShowScheduleExactAlarmPermissionIntent -> {
                permissionManager.requestScheduleExactAlarmPermission()
            }
            AppEvent.CheckNotificationPermissions -> {
                viewModelScope.launch { permissionManager.requestPermissions() }
            }
            AppEvent.IncrementPostNotificationDenialCount -> {
                viewModelScope.launch { appPreferences.incrementPostNotificationDenialCount() }
            }

            AppEvent.ShowTutorialDialog -> updateState { copy(isTutorialDialogVisible = true, isMenuDialogVisible = false) }
            AppEvent.HideTutorialDialog -> updateState { copy(isTutorialDialogVisible = false) }
            AppEvent.DisableTutorialDialog -> {
                viewModelScope.launch { appPreferences.disableTutorialDialog() }
                AppEvent.HideTutorialDialog
            }

            AppEvent.ShowFeedbackDialog -> updateState { copy(isFeedbackDialogVisible = true, isMenuDialogVisible = false) }
            AppEvent.HideFeedbackDialog -> {
                updateState { copy(isFeedbackDialogVisible = false) }
            }
            AppEvent.UploadFeedbackText -> {
                uploadFeedback(state.value.feedbackText)
            }
            AppEvent.ClearFeedbackText -> {
                // I'll pay $10 to whoever can figure out why this doesn't work without the coroutine and delay
                CoroutineScope(Dispatchers.IO).launch {
                    delay(1000)
                    _state.update { it.copy(feedbackText = "") }
                    Log.e("MYTAG", "Uploadtext/clear")
                }
            }
            is AppEvent.SetFeedbackText -> {
                _state.update { it.copy(feedbackText = event.feedbackText) }
                Log.e("MYTAG", "set")
            }
        }
    }

    private inline fun updateState(update: AppState.() -> AppState) {
        _state.update { it.update() }
    }

    private fun uploadFeedback(feedbackText: String) {
        val db = Firebase.firestore
        val feedback = hashMapOf(
            "feedbackText" to feedbackText,
            "timestamp" to FieldValue.serverTimestamp()
        )

        db.collection("feedbacks").add(feedback)
            .addOnSuccessListener {
                // Successful upload
            }
            .addOnFailureListener { e ->
                // Handle error
            }
    }
}

class AppViewModelFactory(private val appPreferences: AppPreferences) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AppViewModel(appPreferences) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
