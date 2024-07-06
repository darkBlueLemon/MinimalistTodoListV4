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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

    // AppViewModel Initialization with AppPreferences Instance (DataStore)
    private val appViewModel by viewModels<AppViewModel> {
        AppViewModelFactory(appPreferences)
    }

    // DataStoreViewModel Initialization with AppPreferences Instance (DataStore)
    private val dataStoreViewModel by viewModels<DataStoreViewModel> {
        PreferencesViewModelFactory(appPreferences)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        installSplashScreen()

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
        // Attaching permission Manager to AppViewModel
        appViewModel.setPermissionManager(permissionManager)

        setContent {
            // Theme from DataStore through dataStoreViewModel
            val theme by dataStoreViewModel.theme.collectAsState()
            val fontFamilyType by dataStoreViewModel.fontFamily.collectAsState()
            val fontSize by dataStoreViewModel.fontSize.collectAsState()
            val fontWeight by dataStoreViewModel.fontWeight.collectAsState()

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
    }

    override fun onResume() {
        super.onResume()
        // Reload tasks when the activity resumes
        taskViewModel.reloadTasks()
    }
}
