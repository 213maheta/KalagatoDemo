package com.twoonethree.kalagatodemo

import com.twoonethree.kalagatodemo.room.Task
import com.twoonethree.kalagatodemo.room.TaskDao

class TaskRepository(private val taskDao: TaskDao) {
    suspend fun addTask(task: Task) = taskDao.insert(task)
    suspend fun updateTask(task: Task) = taskDao.update(task)
    suspend fun deleteTask(task: Task) = taskDao.delete(task)
    suspend fun getTaskById(taskId: Int): Task = taskDao.getTaskById(taskId)

    suspend fun getAllTasks(sortType: SortType): List<Task> {
        return when (sortType) {
            SortType.PRIORITY -> taskDao.getAllTasksByPriority()
            SortType.DUE_DATE -> taskDao.getAllTasksByDueDate()
            SortType.ALPHABETICAL -> taskDao.getAllTasksAlphabetically()
        }
    }

    suspend fun getTasksByCompletion(isCompleted: Boolean): List<Task> {
        return taskDao.getTasksByCompletion(isCompleted)
    }
}

enum class SortType {
    PRIORITY, DUE_DATE, ALPHABETICAL
}