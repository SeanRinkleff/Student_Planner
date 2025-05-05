package com.example.studentplanner.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.studentplanner.viewmodel.TaskViewModel
import com.example.studentplanner.ui.screens.CalendarScreen
import com.example.studentplanner.ui.screens.CourseScreen
import com.example.studentplanner.ui.screens.ScheduleScreen
import com.example.studentplanner.ui.screens.TaskScreen
import com.example.studentplanner.viewmodel.CourseViewModel

sealed class Screen(val route: String) {
    object Task : Screen("task")
    object Course : Screen("course")
    object Schedule : Screen("schedule")
    object Calendar : Screen("calendar")
}

@Composable
fun NavGraph(
    navController: NavHostController,
    taskViewModel: TaskViewModel,
    courseViewModel: CourseViewModel
) {
    NavHost(navController = navController, startDestination = Screen.Task.route) {
        composable(Screen.Task.route) {
            TaskScreen(
                taskViewModel = taskViewModel,
                courseViewModel = courseViewModel,
                navController = navController)
        }
        composable(Screen.Course.route) {
            CourseScreen(
                courseViewModel = courseViewModel,
                navController = navController)
        }
        composable(Screen.Schedule.route) {
            ScheduleScreen()
        }
        composable(Screen.Calendar.route) {
            CalendarScreen()
        }
    }
}

