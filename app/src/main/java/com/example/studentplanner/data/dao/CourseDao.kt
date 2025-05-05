package com.example.studentplanner.data.dao

import androidx.room.*
import com.example.studentplanner.data.model.Course
import kotlinx.coroutines.flow.Flow

@Dao
interface CourseDao {
    @Query("SELECT * FROM courses")
    fun getAllCourses(): Flow<List<Course>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCourse(course: Course)

    @Delete
    suspend fun deleteCourse(course: Course)
}
