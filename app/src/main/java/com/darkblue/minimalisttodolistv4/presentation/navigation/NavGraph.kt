package com.darkblue.minimalisttodolistv4.presentation.navigation

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
import com.darkblue.minimalisttodolistv4.presentation.viewmodel.DataStoreViewModel
import com.darkblue.minimalisttodolistv4.presentation.screens.TaskScreen
import com.darkblue.minimalisttodolistv4.presentation.viewmodel.AppViewModel
import com.darkblue.minimalisttodolistv4.presentation.viewmodel.TaskViewModel
import com.darkblue.minimalisttodolistv4.ui.theme.NoRippleTheme

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavGraph(startDestination: String = "task_list", taskViewModel: TaskViewModel, dataStoreViewModel: DataStoreViewModel, appViewModel: AppViewModel) {
    val navController = rememberNavController()
    val taskState by taskViewModel.state.collectAsState()
    val appState by appViewModel.state.collectAsState()
    NavHost(navController = navController, startDestination = startDestination) {
        composable("task_list") {
            CompositionLocalProvider (
                LocalRippleTheme provides NoRippleTheme
            ){
                TaskScreen(
                    taskState = taskState,
                    onEvent = taskViewModel::onEvent,
                    taskViewModel = taskViewModel,
                    dataStoreViewModel = dataStoreViewModel,
                    onAppEvent = appViewModel::onEvent,
                    appState = appState
                )
            }
        }
    }
}