package com.example.studentplanner.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.studentplanner.viewmodel.TaskViewModel
import com.example.studentplanner.viewmodel.CourseViewModel
import com.example.studentplanner.data.model.Course
import com.example.studentplanner.data.model.Task
import com.example.studentplanner.util.NotificationUtils.scheduleNotification

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaskScreen(
    taskViewModel: TaskViewModel,
    courseViewModel: CourseViewModel,
    navController: NavHostController
) {
    val tasks by taskViewModel.tasks.collectAsState()
    val courses by courseViewModel.courses.collectAsState()
    val context = LocalContext.current

    var showDialog by remember { mutableStateOf(false) }
    var taskToEdit by remember { mutableStateOf<Task?>(null) }

    // Filters state
    var filterDialogVisible by remember { mutableStateOf(false) }
    var filterCompletion by remember { mutableStateOf<String>("All") }
    var filterCourseId by remember { mutableStateOf<Int?>(null) }

    // Filtered tasks
    val filteredTasks = tasks.filter { task ->
        val matchesCompletion = when (filterCompletion) {
            "Completed" -> task.completed
            "Uncompleted" -> !task.completed
            else -> true
        }
        val matchesCourse = filterCourseId == null || task.courseId == filterCourseId
        matchesCompletion && matchesCourse
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    taskToEdit = null
                    showDialog = true
                },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "My Tasks",
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Row {
                    TextButton(onClick = { filterDialogVisible = true }) {
                        Text("Filters")
                    }
                    TextButton(onClick = { navController.navigate("course") }) {
                        Text("Manage Courses")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(filteredTasks) { task ->
                    TaskCard(task, courses, taskViewModel, onEdit = {
                        taskToEdit = it
                        showDialog = true
                    })
                }
            }
        }
    }

    if (showDialog) {
        AddTaskDialog(
            courses = courses,
            existingTask = taskToEdit,
            onAdd = { title, description, dueDate, courseId ->
                taskViewModel.addTask(title, description, dueDate, courseId)
                showDialog = false
            },
            onUpdate = { updatedTask ->
                taskViewModel.updateTask(updatedTask)
                showDialog = false
            },
            onDismiss = {
                showDialog = false
                taskToEdit = null
            }
        )
    }

    if (filterDialogVisible) {
        FilterDialog(
            currentCompletion = filterCompletion,
            currentCourseId = filterCourseId,
            courses = courses,
            onApply = { newCompletion, newCourseId ->
                filterCompletion = newCompletion
                filterCourseId = newCourseId
                filterDialogVisible = false
            },
            onDismiss = { filterDialogVisible = false },
            onReset = {
                filterCompletion = "All"
                filterCourseId = null
                filterDialogVisible = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDialog(
    currentCompletion: String,
    currentCourseId: Int?,
    courses: List<Course>,
    onApply: (String, Int?) -> Unit,
    onDismiss: () -> Unit,
    onReset: () -> Unit
) {
    var selectedCompletion by remember { mutableStateOf(currentCompletion) }
    var selectedCourseId by remember { mutableStateOf(currentCourseId) }
    var courseDropdownExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = { onApply(selectedCompletion, selectedCourseId) }) {
                Text("Apply")
            }
        },
        dismissButton = {
            Row {
                TextButton(onClick = onReset) {
                    Text("Reset")
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        },
        title = { Text("Filter Tasks") },
        text = {
            Column {
                Text("Completion Status:")

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedCompletion == "All",
                            onClick = { selectedCompletion = "All" }
                        )
                        Text("All")
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        listOf("Completed", "Uncompleted").forEach { option ->
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                RadioButton(
                                    selected = selectedCompletion == option,
                                    onClick = { selectedCompletion = option }
                                )
                                Text(option)
                                Spacer(modifier = Modifier.width(16.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Filter by Course:")

                ExposedDropdownMenuBox(
                    expanded = courseDropdownExpanded,
                    onExpandedChange = { courseDropdownExpanded = !courseDropdownExpanded }
                ) {
                    OutlinedTextField(
                        readOnly = true,
                        value = selectedCourseId?.let { id ->
                            courses.find { it.id == id }?.name ?: "Select Course"
                        } ?: "All Courses",
                        onValueChange = {},
                        label = { Text("Course") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(courseDropdownExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = courseDropdownExpanded,
                        onDismissRequest = { courseDropdownExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("All Courses") },
                            onClick = {
                                selectedCourseId = null
                                courseDropdownExpanded = false
                            }
                        )
                        courses.forEach { course ->
                            DropdownMenuItem(
                                text = { Text(course.name) },
                                onClick = {
                                    selectedCourseId = course.id
                                    courseDropdownExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskDialog(
    courses: List<Course>,
    onAdd: (String, String, String, Int?) -> Unit,
    onUpdate: (Task) -> Unit,
    onDismiss: () -> Unit,
    existingTask: Task? = null
) {
    val context = LocalContext.current

    var title by remember { mutableStateOf(existingTask?.title ?: "") }
    var description by remember { mutableStateOf(existingTask?.description ?: "") }
    var dueDate by remember { mutableStateOf(existingTask?.dueDate ?: "") }
    var dueDateError by remember { mutableStateOf(false) }
    var selectedCourseId by remember { mutableStateOf(existingTask?.courseId) }
    var expanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                // Validate the due date format. Empty dueDate is allowed.
                val dateRegex = Regex("""\d{2}/\d{2}/\d{4}""")
                dueDateError = dueDate.isNotBlank() && !dateRegex.matches(dueDate)

                if (title.isNotBlank() && !dueDateError) {
                    if (existingTask != null) {
                        val updatedTask = existingTask.copy(
                            title = title.trim(),
                            description = description.trim(),
                            dueDate = dueDate.trim(),
                            courseId = selectedCourseId
                        )
                        onUpdate(updatedTask)
                        scheduleNotification(context, title.trim(), dueDate.trim())
                    } else {
                        onAdd(title.trim(), description.trim(), dueDate.trim(), selectedCourseId)
                        scheduleNotification(context, title.trim(), dueDate.trim())
                    }
                    onDismiss()
                }
            }) {
                Text(if (existingTask != null) "Update" else "Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = { Text(if (existingTask != null) "Edit Task" else "New Task") },
        text = {
            Column {
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = dueDate,
                    onValueChange = {
                        dueDate = it
                        dueDateError = it.isNotBlank() && !Regex("""\d{2}/\d{2}/\d{4}""").matches(it)
                    },
                    isError = dueDateError,
                    label = { Text("Due Date (mm/dd/yyyy)") },
                    modifier = Modifier.fillMaxWidth(),
                    supportingText = {
                        if (dueDateError)
                            Text("Enter a valid date (mm/dd/yyyy)", color = MaterialTheme.colorScheme.error)
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        readOnly = true,
                        value = courses.firstOrNull { it.id == selectedCourseId }?.name ?: "Select Course",
                        onValueChange = {},
                        label = { Text("Course") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        courses.forEach { course ->
                            DropdownMenuItem(
                                text = { Text(course.name) },
                                onClick = {
                                    selectedCourseId = course.id
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }
    )
}



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TaskCard(
    task: Task,
    courses: List<Course>,
    taskViewModel: TaskViewModel,
    onEdit: (Task) -> Unit
) {
    val courseName = courses.find { it.id == task.courseId }?.name ?: "No Course"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = task.title, style = MaterialTheme.typography.titleMedium)
            Text(text = "Course: $courseName", style = MaterialTheme.typography.bodySmall)
            if (!task.description.isNullOrBlank()) {
                Text(text = task.description, style = MaterialTheme.typography.bodyMedium)
            }
            if (!task.dueDate.isNullOrBlank()) {
                Text(text = "Due: ${task.dueDate}", style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = task.completed,
                        onCheckedChange = {
                            taskViewModel.updateTask(task.copy(completed = it))
                        }
                    )
                    Text("Completed")
                }
                Row {
                    TextButton(onClick = { onEdit(task) }) {
                        Text("Edit")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = { taskViewModel.removeTask(task) }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete Task"
                        )
                    }
                }
            }
        }
    }
}

