package com.darkblue.minimalisttodolistv4.presentation

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
import com.darkblue.minimalisttodolistv4.ui.theme.NoRippleTheme

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NavGraph(startDestination: String = "task_list", viewModel: TaskViewModel) {
    val navController = rememberNavController()
    val state by viewModel.state.collectAsState()
    NavHost(navController = navController, startDestination = startDestination) {
        composable("task_list") {
            CompositionLocalProvider (
                LocalRippleTheme provides NoRippleTheme
            ){
                TaskScreen(state = state, onEvent = viewModel::onEvent, navController = navController, viewModel = viewModel)
            }
        }
    }
}