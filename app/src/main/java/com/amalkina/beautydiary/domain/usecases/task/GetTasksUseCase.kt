package com.amalkina.beautydiary.domain.usecases.task

import com.amalkina.beautydiary.domain.interfaces.TaskRepository
import com.amalkina.beautydiary.domain.models.Task
import com.amalkina.beautydiary.domain.usecases.common.BaseUseCase
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.inject


class GetTasksUseCase : BaseUseCase() {
    private val repository by inject<TaskRepository>()

    fun all() = try {
        Result.Ok(repository.getAllTasks())
    } catch (ex: Exception) {
        Result.Error(ex.localizedMessage ?: ex.message ?: "")
    }

    fun categoryTasks(categoryId: Long) = try {
        Result.Ok(repository.getCategoryTasks(categoryId))
    } catch (ex: Exception) {
        Result.Error(ex.localizedMessage ?: ex.message ?: "")
    }

    sealed class Result {
        data class Ok(val response: Flow<List<Task>>) : Result()
        data class Error(val error: String) : Result()

    }
}