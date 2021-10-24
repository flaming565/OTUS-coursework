package com.amalkina.beautydiary.domain.interfaces

import com.amalkina.beautydiary.domain.models.DomainTask
import com.amalkina.beautydiary.domain.models.DomainTaskAndCategory
import kotlinx.coroutines.flow.Flow

internal interface TaskRepository {

    suspend fun getTask(id: Long): DomainTask

    fun getAllTasks(): Flow<List<DomainTask>>

    fun getBaseTasks(baseCategoryId: Long): Flow<List<DomainTask>>

    suspend fun getCategoryTask(id: Long): DomainTaskAndCategory

    suspend fun createTask(task: DomainTask)

    suspend fun updateTask(task: DomainTask)

    suspend fun deleteTask(task: DomainTask)
}