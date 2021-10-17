package com.amalkina.beautydiary.domain.usecases.task

import com.amalkina.beautydiary.domain.interfaces.TaskRepository
import com.amalkina.beautydiary.domain.models.Task
import com.amalkina.beautydiary.domain.usecases.common.BaseUseCase
import org.koin.core.component.inject


class UpdateTaskUseCase : BaseUseCase() {
    private val repository by inject<TaskRepository>()

    suspend fun create(task: Task): Result {
        return try {
            repository.createTask(task)
            Result.Ok
        } catch (ex: Exception) {
            Result.Error(ex.localizedMessage ?: ex.message ?: "")
        }
    }

    suspend fun update(task: Task): Result {
        return try {
            repository.updateTask(task)
            Result.Ok
        } catch (ex: Exception) {
            Result.Error(ex.localizedMessage ?: ex.message ?: "")
        }
    }

    suspend fun delete(task: Task): Result {
        return try {
            repository.deleteTask(task)
            Result.Ok
        } catch (ex: Exception) {
            Result.Error(ex.localizedMessage ?: ex.message ?: "")
        }
    }

    sealed class Result {
        object Ok : Result()
        data class Error(val error: String) : Result()
    }
}