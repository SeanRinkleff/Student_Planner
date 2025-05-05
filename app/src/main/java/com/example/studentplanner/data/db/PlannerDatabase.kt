package com.example.studentplanner.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.studentplanner.data.dao.TaskDao
import com.example.studentplanner.data.model.Task

@Database(entities = [Task::class], version = 1)
abstract class PlannerDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}