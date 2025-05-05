package com.example.studentplanner.viewmodel

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.studentplanner.data.model.Task
import com.example.studentplanner.data.TaskRepository
import com.example.studentplanner.worker.TaskReminderWorker
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@RequiresApi(Build.VERSION_CODES.O)
class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> = _tasks.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAllTasks().collect { taskList ->
                _tasks.value = taskList.sortedWith(compareBy {
                    parseDate(it.dueDate) ?: LocalDate.MAX
                })
            }
        }
    }

    fun addTask(title: String, description: String, dueDate: String, courseId: Int?) {
        val newTask = Task(
            title = title,
            description = description,
            dueDate = dueDate,
            courseId = courseId
        )
        viewModelScope.launch {
            repository.insertTask(newTask)
        }
    }

    fun removeTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }

    fun updateTask(updatedTask: Task) {
        viewModelScope.launch {
            repository.updateTask(updatedTask)
        }
    }

    fun updateTaskCompletion(task: Task, completed: Boolean) {
        val updatedTask = task.copy(completed = completed)
        viewModelScope.launch {
            repository.updateTask(updatedTask)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun parseDate(dateStr: String): LocalDate? {
        return try {
            if (dateStr.isBlank()) null
            else LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("MM/dd/yyyy"))
        } catch (e: DateTimeParseException) {
            null
        }
    }

    fun scheduleReminder(context: Context, taskTitle: String, dueDate: String) {
        val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.US)
        val due = dateFormat.parse(dueDate)
        val now = Date()

        val delay = due.time - now.time - TimeUnit.HOURS.toMillis(1) // 1 hour before

        if (delay > 0) {
            val data = workDataOf("task_title" to taskTitle)

            val reminderRequest = OneTimeWorkRequestBuilder<TaskReminderWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(data)
                .build()

            WorkManager.getInstance(context).enqueue(reminderRequest)
        }
    }
}
