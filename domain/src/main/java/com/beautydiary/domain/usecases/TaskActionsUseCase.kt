package com.beautydiary.domain.usecases

import com.beautydiary.domain.interfaces.TaskRepository
import com.beautydiary.domain.models.DomainTask
import org.koin.core.component.inject


class TaskActionsUseCase : BaseUseCase() {
    private val repository by inject<TaskRepository>()

    fun all() =
        flowResult(Unit) { repository.getAllTasks() }

    fun getBaseTasks(baseCategoryId: Long) =
        flowResult(baseCategoryId) { repository.getBaseTasks(baseCategoryId) }

    fun categoryTask(id: Long) =
        flowResult(id) { repository.getCategoryTask(id) }

    fun todayTasksSync() = repository.getAllTasksSync().filter { it.daysRemaining <= 1 }

    suspend fun get(id: Long) =
        suspendResult(id) { repository.getTask(id) }

    suspend fun create(task: DomainTask) =
        suspendResult(task) { repository.createTask(task) }

    suspend fun update(task: DomainTask) =
        suspendResult(task) { repository.updateTask(task) }

    suspend fun delete(task: DomainTask) =
        suspendResult(task) { repository.deleteTask(task) }

}