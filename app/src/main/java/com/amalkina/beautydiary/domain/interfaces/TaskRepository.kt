package com.amalkina.beautydiary.domain.interfaces

import com.amalkina.beautydiary.domain.models.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {

    fun getTask(id: Long): Flow<Task>

    fun getAllTasks(): Flow<List<Task>>

    fun getCategoryTasks(categoryId: Long): Flow<List<Task>>

    suspend fun createTask(task: Task)

    suspend fun updateTask(task: Task)

    suspend fun deleteTask(task: Task)
}