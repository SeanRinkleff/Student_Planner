package com.example.studentplanner.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.studentplanner.data.CourseRepository
import com.example.studentplanner.data.model.Course
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CourseViewModel(private val repository: CourseRepository) : ViewModel() {
    val courses = repository.allCourses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addCourse(name: String) {
        viewModelScope.launch {
            repository.insert(Course(name = name))
        }
    }

    fun deleteCourse(course: Course) {
        viewModelScope.launch {
            repository.delete(course)
        }
    }
}
