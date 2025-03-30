package com.twoonethree.kalagatodemo.navsetup

import TaskAddScreen
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.twoonethree.kalagatodemo.LocalDataStore
import com.twoonethree.kalagatodemo.TaskViewModel
import com.twoonethree.kalagatodemo.screens.HomeScreen
import com.twoonethree.kalagatodemo.screens.SettingScreen
import com.twoonethree.kalagatodemo.screens.TaskDetailScreen
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun NavigationSetUp(isDarkTheme: MutableState<Boolean>, primaryColor: MutableState<Long>)
{
    val navController = rememberNavController()
    val taskViewModel: TaskViewModel = koinViewModel()
    val localDataStore: LocalDataStore = koinInject()

    NavHost(navController = navController, startDestination = ScreenName.HomeScreen) {
        composable<ScreenName.HomeScreen> {
            HomeScreen(navController = navController, vm = taskViewModel)
        }
        composable<ScreenName.TaskAddScreen> {
            TaskAddScreen(navController = navController, vm = taskViewModel)
        }
        composable<ScreenName.TaskViewScreen> {
            val taskId = it.toRoute<ScreenName.TaskViewScreen>().taskId
            TaskDetailScreen(navController = navController, vm = taskViewModel,taskId = taskId)
        }
        composable<ScreenName.SettingScreen> {
            SettingScreen(navController, isDarkTheme, primaryColor, localDataStore)
        }
    }
}

