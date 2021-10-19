package com.amalkina.beautydiary.domain.usecases.task

import com.amalkina.beautydiary.domain.common.Result
import com.amalkina.beautydiary.domain.interfaces.TaskRepository
import com.amalkina.beautydiary.domain.usecases.common.BaseUseCase
import org.koin.core.component.inject


class GetTaskUseCase : BaseUseCase() {
    private val repository by inject<TaskRepository>()

    fun get(id: Long): Result {
        return try {
            Result.Success(repository.getTask(id))
        } catch (ex: Exception) {
            Result.Error(ex.localizedMessage ?: ex.message ?: "")
        }
    }
}