package com.darkblue.minimalisttodolistv4

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import com.darkblue.minimalisttodolistv4.data.AppPreferences
import com.darkblue.minimalisttodolistv4.data.ContactDatabase
import com.darkblue.minimalisttodolistv4.data.ThemeType
import com.darkblue.minimalisttodolistv4.presentation.NavGraph
import com.darkblue.minimalisttodolistv4.presentation.PreferencesViewModel
import com.darkblue.minimalisttodolistv4.presentation.PreferencesViewModelFactory
import com.darkblue.minimalisttodolistv4.presentation.TaskViewModel
import com.darkblue.minimalisttodolistv4.ui.theme.MinimalistTodoListV4Theme

class MainActivity : ComponentActivity() {
    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            ContactDatabase::class.java,
            "contacts.db"
        ).build()
    }
    private val taskViewModel by viewModels<TaskViewModel>(
        factoryProducer = {
            object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return TaskViewModel(db.dao) as T
                }
            }
        }
    )
    private lateinit var appPreferences: AppPreferences
    private val preferencesViewModel by viewModels<PreferencesViewModel> {
        PreferencesViewModelFactory(appPreferences)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        appPreferences = AppPreferences(this)

        setContent {
            val theme by preferencesViewModel.theme.collectAsState()
            var darkTheme by remember { mutableStateOf(false) }

            // Update darkTheme based on collected theme
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
                NavGraph(taskViewModel = taskViewModel, preferencesViewModel = preferencesViewModel)
            }
        }
    }
}
