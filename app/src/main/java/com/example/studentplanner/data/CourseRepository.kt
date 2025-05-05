package com.example.studentplanner.data

import com.example.studentplanner.data.dao.CourseDao
import com.example.studentplanner.data.model.Course

class CourseRepository(private val courseDao: CourseDao) {
    val allCourses = courseDao.getAllCourses()

    suspend fun insert(course: Course) {
        courseDao.insertCourse(course)
    }

    suspend fun delete(course: Course) {
        courseDao.deleteCourse(course)
    }
}
