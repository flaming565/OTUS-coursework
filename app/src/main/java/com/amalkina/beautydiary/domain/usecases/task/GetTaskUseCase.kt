package com.amalkina.beautydiary.domain.usecases.task

import com.amalkina.beautydiary.domain.interfaces.TaskRepository
import com.amalkina.beautydiary.domain.models.Task
import com.amalkina.beautydiary.domain.usecases.common.BaseUseCase
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.inject


class GetTaskUseCase : BaseUseCase() {
    private val repository by inject<TaskRepository>()

    fun get(id: Long): Result {
        return try {
            Result.Ok(repository.getTask(id))
        } catch (ex: Exception) {
            Result.Error(ex.localizedMessage ?: ex.message ?: "")
        }
    }

    sealed class Result {
        data class Ok(val response: Flow<Task>) : Result()
        data class Error(val error: String) : Result()

    }
}