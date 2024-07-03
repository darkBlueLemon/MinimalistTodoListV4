package com.darkblue.minimalisttodolistv4

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.darkblue.minimalisttodolistv4.data.preferences.AppPreferences
import com.darkblue.minimalisttodolistv4.data.database.ContactDatabase
import com.darkblue.minimalisttodolistv4.data.model.ThemeType
import com.darkblue.minimalisttodolistv4.ui.navigation.NavGraph
import com.darkblue.minimalisttodolistv4.viewmodel.AppViewModel
import com.darkblue.minimalisttodolistv4.viewmodel.DataStoreViewModel
import com.darkblue.minimalisttodolistv4.viewmodel.PreferencesViewModelFactory
import com.darkblue.minimalisttodolistv4.viewmodel.TaskViewModel
import com.darkblue.minimalisttodolistv4.ui.theme.MinimalistTodoListV4Theme
import com.darkblue.minimalisttodolistv4.util.NotificationHelper
import com.darkblue.minimalisttodolistv4.util.PermissionManager

class MainActivity : ComponentActivity() {

    // Room Database Initialization
    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            ContactDatabase::class.java,
            "contacts.db"
        ).build()
    }

    // Notification Helper Initialization
    private val notificationHelper by lazy {
        NotificationHelper(applicationContext)
    }

    // TaskViewModel Initialization with Database and Notification Instance
    private val taskViewModel by viewModels<TaskViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return TaskViewModel(db.dao, notificationHelper) as T
                }
            }
        }
    )

    private lateinit var permissionManager: PermissionManager
    private lateinit var postNotificationPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var appPreferences: AppPreferences

    // AppViewModel Initialization
    private val appViewModel = AppViewModel()

    // DataStoreViewModel Initialization with AppPreferences Instance (DataStore)
    private val dataStoreViewModel by viewModels<DataStoreViewModel> {
        PreferencesViewModelFactory(appPreferences)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Setting Status Bar and Navigation Bar to be Transparent
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                        Color.TRANSPARENT , Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.light(
                Color.TRANSPARENT, Color.TRANSPARENT
            )
        )

        // Initializing appPreferences
        appPreferences = AppPreferences(this)

        // Initializing Notification Launcher / Permission Manager
        postNotificationPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            permissionManager.handlePostNotificationPermissionResult(isGranted)
        }
        permissionManager = PermissionManager(
            context = this,
            activity = this,
            postNotificationPermissionLauncher = postNotificationPermissionLauncher,
            onTaskEvent = appViewModel::onEvent
        )
        // Attaching permission Manager to AppViewModel
        appViewModel.setPermissionManager(permissionManager)

        setContent {

            // Theme from DataStore through dataStoreViewModel
            val theme by dataStoreViewModel.theme.collectAsState()
            var darkTheme by remember { mutableStateOf(false) }
            darkTheme = when (theme) {
                ThemeType.DARK -> true
                ThemeType.LIGHT -> false
                ThemeType.AUTO -> {
                    val nightModeFlags = resources.configuration.uiMode and
                            Configuration.UI_MODE_NIGHT_MASK
                    nightModeFlags == Configuration.UI_MODE_NIGHT_YES
                }
            }

            MinimalistTodoListV4Theme(darkTheme = darkTheme) {
                NavGraph(
                    taskViewModel = taskViewModel,
                    dataStoreViewModel = dataStoreViewModel,
                    appViewModel = appViewModel
                )
            }
        }
    }
}
