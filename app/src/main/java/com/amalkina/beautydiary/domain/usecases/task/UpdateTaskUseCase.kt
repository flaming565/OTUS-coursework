package com.amalkina.beautydiary.domain.usecases.task

import com.amalkina.beautydiary.domain.common.Result
import com.amalkina.beautydiary.domain.interfaces.TaskRepository
import com.amalkina.beautydiary.domain.models.Task
import com.amalkina.beautydiary.domain.usecases.common.BaseUseCase
import org.koin.core.component.inject


class UpdateTaskUseCase : BaseUseCase() {
    private val repository by inject<TaskRepository>()

    suspend fun create(task: Task): Result {
        return try {
            repository.createTask(task)
            Result.Success(Unit)
        } catch (ex: Exception) {
            Result.Error(ex.localizedMessage ?: ex.message ?: "")
        }
    }

    suspend fun update(task: Task): Result {
        return try {
            repository.updateTask(task)
            Result.Success(Unit)
        } catch (ex: Exception) {
            Result.Error(ex.localizedMessage ?: ex.message ?: "")
        }
    }

    suspend fun delete(task: Task): Result {
        return try {
            repository.deleteTask(task)
            Result.Success(Unit)
        } catch (ex: Exception) {
            Result.Error(ex.localizedMessage ?: ex.message ?: "")
        }
    }
}