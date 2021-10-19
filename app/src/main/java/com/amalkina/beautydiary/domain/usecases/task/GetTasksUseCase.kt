package com.amalkina.beautydiary.domain.usecases.task

import com.amalkina.beautydiary.domain.common.Result
import com.amalkina.beautydiary.domain.interfaces.TaskRepository
import com.amalkina.beautydiary.domain.usecases.common.BaseUseCase
import org.koin.core.component.inject


class GetTasksUseCase : BaseUseCase() {
    private val repository by inject<TaskRepository>()

    fun all() = try {
        Result.Success(repository.getAllTasks())
    } catch (ex: Exception) {
        Result.Error(ex.localizedMessage ?: ex.message ?: "")
    }

    fun categoryTasks(categoryId: Long) = try {
        Result.Success(repository.getCategoryTasks(categoryId))
    } catch (ex: Exception) {
        Result.Error(ex.localizedMessage ?: ex.message ?: "")
    }
}