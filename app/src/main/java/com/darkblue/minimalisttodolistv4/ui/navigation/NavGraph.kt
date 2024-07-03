package com.darkblue.minimalisttodolistv4.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.darkblue.minimalisttodolistv4.viewmodel.DataStoreViewModel
import com.darkblue.minimalisttodolistv4.ui.screens.TaskScreen
import com.darkblue.minimalisttodolistv4.viewmodel.AppViewModel
import com.darkblue.minimalisttodolistv4.viewmodel.TaskViewModel
import com.darkblue.minimalisttodolistv4.ui.theme.NoRippleTheme

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavGraph(
    startDestination: String = "task_list",
    taskViewModel: TaskViewModel,
    dataStoreViewModel: DataStoreViewModel,
    appViewModel: AppViewModel
) {
    val navController = rememberNavController()
    val taskState by taskViewModel.state.collectAsState()
    val appState by appViewModel.state.collectAsState()
    NavHost(navController, startDestination) {
        composable("task_list") {

            // Disabling Ripple Effect on Button Press Globally
            CompositionLocalProvider (
                LocalRippleTheme provides NoRippleTheme
            ){
                TaskScreen(
                    taskState = taskState,
                    appState = appState,
                    onEvent = taskViewModel::onEvent,
                    onAppEvent = appViewModel::onEvent,
                    taskViewModel = taskViewModel,
                    dataStoreViewModel = dataStoreViewModel,
                )
            }
        }
    }
}