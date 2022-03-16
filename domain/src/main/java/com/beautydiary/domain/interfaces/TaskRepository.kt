package com.beautydiary.domain.interfaces

import com.beautydiary.domain.models.DomainTask
import com.beautydiary.domain.models.DomainTaskAndCategory
import kotlinx.coroutines.flow.Flow

interface TaskRepository {

    suspend fun getTask(id: Long): DomainTask

    fun getAllTasks(): Flow<List<DomainTask>>

    fun getAllTasksSync(): List<DomainTask>

    fun getBaseTasks(baseCategoryId: Long): Flow<List<DomainTask>>

    fun getCategoryTask(id: Long): Flow<DomainTaskAndCategory>

    suspend fun createTask(task: DomainTask)

    suspend fun updateTask(task: DomainTask)

    suspend fun deleteTask(task: DomainTask)
}