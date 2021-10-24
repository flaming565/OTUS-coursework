package com.amalkina.beautydiary.domain.usecases

import com.amalkina.beautydiary.domain.interfaces.TaskRepository
import com.amalkina.beautydiary.domain.models.DomainTask
import org.koin.core.component.inject


internal class TaskActionsUseCase : BaseUseCase() {
    private val repository by inject<TaskRepository>()

    fun all() =
        flowResult(Unit) { repository.getAllTasks() }

    fun getBaseTasks(baseCategoryId: Long) =
        flowResult(baseCategoryId) { repository.getBaseTasks(baseCategoryId) }

    suspend fun categoryTask(id: Long) =
        suspendResult(id) { repository.getCategoryTask(id) }

    suspend fun get(id: Long) =
        suspendResult(id) { repository.getTask(id) }

    suspend fun create(task: DomainTask) =
        suspendResult(task) { repository.createTask(task) }

    suspend fun update(task: DomainTask) =
        suspendResult(task) { repository.updateTask(task) }

    suspend fun delete(task: DomainTask) =
        suspendResult(task) { repository.deleteTask(task) }

}