package com.example.studentplanner.data

import com.example.studentplanner.data.dao.TaskDao
import com.example.studentplanner.data.model.Task
import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {
    fun getAllTasks(): Flow<List<Task>> = taskDao.getAll()
    suspend fun insertTask(task: Task) = taskDao.insert(task)
    suspend fun deleteTask(task: Task) = taskDao.delete(task)
    suspend fun updateTask(task: Task) = taskDao.updateTask(task)
}