package com.twoonethree.kalagatodemo.room

import androidx.room.*

@Dao
interface TaskDao {
    @Insert
    suspend fun insert(task: Task): Long

    @Update
    suspend fun update(task: Task)

    @Delete
    suspend fun delete(task: Task)

    @Query("SELECT * FROM tasks ORDER BY CASE priority WHEN 'HIGH' THEN 1 WHEN 'MEDIUM' THEN 2 WHEN 'LOW' THEN 3 END")
    suspend fun getAllTasksByPriority(): List<Task>

    @Query("SELECT * FROM tasks ORDER BY dueDate ASC")
    suspend fun getAllTasksByDueDate(): List<Task>

    @Query("SELECT * FROM tasks ORDER BY title COLLATE NOCASE ASC")
    suspend fun getAllTasksAlphabetically(): List<Task>

    @Query("SELECT * FROM tasks WHERE isCompleted = :isCompleted")
    suspend fun getTasksByCompletion(isCompleted: Boolean): List<Task>

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: Int): Task
}