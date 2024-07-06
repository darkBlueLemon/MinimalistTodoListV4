package com.darkblue.minimalisttodolistv4

import android.app.Activity
import android.content.ComponentName
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.room.Room
import com.darkblue.minimalisttodolistv4.data.preferences.AppPreferences
import com.darkblue.minimalisttodolistv4.data.database.ContactDatabase
import com.darkblue.minimalisttodolistv4.data.model.FontFamilyType
import com.darkblue.minimalisttodolistv4.data.model.ThemeType
import com.darkblue.minimalisttodolistv4.ui.navigation.NavGraph
import com.darkblue.minimalisttodolistv4.viewmodel.AppViewModel
import com.darkblue.minimalisttodolistv4.viewmodel.DataStoreViewModel
import com.darkblue.minimalisttodolistv4.viewmodel.PreferencesViewModelFactory
import com.darkblue.minimalisttodolistv4.viewmodel.TaskViewModel
import com.darkblue.minimalisttodolistv4.ui.theme.MinimalistTodoListV4Theme
import com.darkblue.minimalisttodolistv4.util.NotificationHelper
import com.darkblue.minimalisttodolistv4.util.PermissionManager
import com.darkblue.minimalisttodolistv4.viewmodel.AppViewModelFactory
import com.darkblue.minimalisttodolistv4.viewmodel.TaskViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var permissionManager: PermissionManager
    private lateinit var postNotificationPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var appPreferences: AppPreferences

    private val appViewModel by viewModels<AppViewModel> {
        AppViewModelFactory(appPreferences)
    }

    private val dataStoreViewModel by viewModels<DataStoreViewModel> {
        PreferencesViewModelFactory(appPreferences)
    }

    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            ContactDatabase::class.java,
            "contacts.db"
        ).build()
    }

    private val notificationHelper by lazy {
        NotificationHelper(applicationContext)
    }

    private val taskViewModel by viewModels<TaskViewModel> {
        TaskViewModelFactory(db.dao, notificationHelper, dataStoreViewModel)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupUI()
        initializeComponents()
        setContent {
            setupTheme()
        }
    }

    private fun setupUI() {
        installSplashScreen()
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.light(Color.TRANSPARENT, Color.TRANSPARENT)
        )
    }

    private fun initializeComponents() {
        appPreferences = AppPreferences.getInstance(this)
        initializeNotificationPermission()
        appViewModel.setPermissionManager(permissionManager)
    }

    private fun initializeNotificationPermission() {
        postNotificationPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            lifecycleScope.launch {
                permissionManager.handlePostNotificationPermissionResult(isGranted)
            }
        }
        permissionManager = PermissionManager(
            context = this,
            activity = this,
            postNotificationPermissionLauncher = postNotificationPermissionLauncher,
            onTaskEvent = appViewModel::onEvent,
            appPreferences = appPreferences
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    private fun setupTheme() {
        val theme by dataStoreViewModel.theme.collectAsState()
        val fontFamilyType by dataStoreViewModel.fontFamily.collectAsState()
        val fontSize by dataStoreViewModel.fontSize.collectAsState()
        val fontWeight by dataStoreViewModel.fontWeight.collectAsState()

        val darkTheme = when (theme) {
            ThemeType.DARK -> true
            ThemeType.LIGHT -> false
            ThemeType.AUTO -> isSystemInDarkTheme()
        }

        MinimalistTodoListV4Theme(
            darkTheme = darkTheme,
            fontFamilyType = fontFamilyType,
            baseFontSize = fontSize,
            fontWeight = fontWeight
        ) {
            NavGraph(
                taskViewModel = taskViewModel,
                dataStoreViewModel = dataStoreViewModel,
                appViewModel = appViewModel
            )
        }
    }

    override fun onResume() {
        super.onResume()
        taskViewModel.reloadTasks()
    }
}