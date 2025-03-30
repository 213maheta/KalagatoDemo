package com.twoonethree.kalagatodemo

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.twoonethree.kalagatodemo.room.Priority
import com.twoonethree.kalagatodemo.room.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.Date

class TaskViewModel(private val repository: TaskRepository) : ViewModel() {

    val tasks = mutableStateListOf<Task>()

    private val _currentSortType = MutableStateFlow(SortType.PRIORITY)
    private val _showCompleted = MutableStateFlow(false)

    private var deletedIndex = -1

    init {
        loadTasks()
    }

    fun loadTasks() {
        viewModelScope.launch {
            val tasks = if (_showCompleted.value) {
                repository.getTasksByCompletion(true)
            } else {
                repository.getAllTasks(_currentSortType.value)
            }
            this@TaskViewModel.tasks.clear()
            this@TaskViewModel.tasks.addAll(tasks)
        }
    }

    fun addTask(title: String, description: String?, priority: Priority, dueDate: Date) {
        viewModelScope.launch {
            val task = Task(
                title = title,
                description = description,
                priority = priority,
                dueDate = dueDate
            )
            repository.addTask(task)
            loadTasks()
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            repository.updateTask(task.copy(isCompleted = true))
            loadTasks()
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
            loadTasks()
        }
    }

    fun markAsCompleteWithUndo(task: Task, isCompleted:Boolean = true) {
        viewModelScope.launch {
            val taskIndex = tasks.indexOfFirst { it.id == task.id }
            taskIndex.let{
                tasks[taskIndex] = task.copy(isCompleted = isCompleted)
                repository.updateTask(task.copy(isCompleted = isCompleted))
            }
        }
    }

    fun deleteWithUndo(task: Task) {
        viewModelScope.launch {
            deletedIndex = tasks.indexOfFirst { it.id == task.id }
            tasks.remove(task)
            repository.deleteTask(task)
        }
    }

    fun deleteUndo(task: Task) {
        viewModelScope.launch {
            tasks.add(deletedIndex, task)
            repository.addTask(task)
            deletedIndex = -1
        }
    }

    fun setSortType(sortType: SortType) {
        _currentSortType.value = sortType
        loadTasks()
    }

    fun setShowCompleted(show: Boolean) {
        _showCompleted.value = show
        loadTasks()
    }

    suspend fun getTaskById(taskId: Int): Task = repository.getTaskById(taskId)
}