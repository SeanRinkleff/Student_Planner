package com.example.studentplanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.studentplanner.data.AppDatabase
import com.example.studentplanner.data.CourseRepository
import com.example.studentplanner.data.TaskRepository
import com.example.studentplanner.ui.navigation.NavGraph
import com.example.studentplanner.ui.theme.StudentPlannerTheme
import com.example.studentplanner.viewmodel.CourseViewModel
import com.example.studentplanner.viewmodel.CourseViewModelFactory
import com.example.studentplanner.viewmodel.TaskViewModel
import com.example.studentplanner.viewmodel.TaskViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(applicationContext)

        val taskRepository = TaskRepository(database.taskDao())
        val courseRepository = CourseRepository(database.courseDao())

        val taskViewModelFactory = TaskViewModelFactory(taskRepository)
        val courseViewModelFactory = CourseViewModelFactory(courseRepository)

        setContent {
            StudentPlannerTheme {
                val navController = rememberNavController()

                val taskViewModel: TaskViewModel = viewModel(factory = taskViewModelFactory)
                val courseViewModel: CourseViewModel = viewModel(
                    factory = CourseViewModelFactory(CourseRepository(database.courseDao()))
                )

                NavGraph(
                    navController = navController,
                    taskViewModel = taskViewModel,
                    courseViewModel = courseViewModel
                )
            }
        }
    }
}
