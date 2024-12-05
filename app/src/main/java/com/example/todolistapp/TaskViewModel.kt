package com.example.todolistapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Represents a user with a name and an email
data class User(val name: String, val email: String)
// Represents a task with a title and details
data class Task(val title: String, val details: String = "")

class TaskViewModel : ViewModel() {
    private val _user = MutableStateFlow(User("Gabriel Castro", "gabriel@example.com"))
    val user: StateFlow<User> = _user

    private val _tasks = MutableStateFlow(listOf<Task>())
    val tasks: StateFlow<List<Task>> = _tasks

    // Updates the user's name and email
    fun updateUser(name: String, email: String) {
        viewModelScope.launch {
            _user.value = User(name, email)
        }
    }

    // Adds a task to the list
    fun addTask(title: String) {
        viewModelScope.launch {
            _tasks.value = _tasks.value + Task(title)
        }
    }

    // Updates the details of a task
    fun updateTaskDetails(index: Int, details: String) {
        viewModelScope.launch {
            _tasks.value = _tasks.value.mapIndexed { i, task ->
                if (i == index) task.copy(details = details) else task
            }
        }
    }

    // Deletes a task from the list
    fun deleteTask(index: Int) {
        viewModelScope.launch {
            _tasks.value = _tasks.value.toMutableList().apply { removeAt(index) }
        }
    }
}