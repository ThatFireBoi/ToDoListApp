package com.example.todolistapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// Represents a user with a name and an email
data class User(val name: String, val email: String)

class TaskViewModel : ViewModel() {
    private val _user = MutableStateFlow(User("Gabriel Castro", "gabriel@example.com"))
    val user: StateFlow<User> = _user

    private val _toDoItems = MutableStateFlow(listOf<String>())
    val toDoItems: StateFlow<List<String>> = _toDoItems

    // Updates the user's name and email
    fun updateUser(name: String, email: String) {
        viewModelScope.launch {
            _user.value = User(name, email)
        }
    }

    // Adds an item to the to-do list
    fun addItem(item: String) {
        viewModelScope.launch {
            _toDoItems.value = _toDoItems.value + item
        }
    }

    // Deletes an item from the to-do list
    fun deleteItem(item: String) {
        viewModelScope.launch {
            _toDoItems.value = _toDoItems.value.filter { it != item }
        }
    }
}