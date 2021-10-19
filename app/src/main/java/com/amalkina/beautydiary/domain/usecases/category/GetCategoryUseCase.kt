package com.amalkina.beautydiary.domain.usecases.category

import com.amalkina.beautydiary.domain.common.Result
import com.amalkina.beautydiary.domain.interfaces.CategoryRepository
import com.amalkina.beautydiary.domain.usecases.common.BaseUseCase
import org.koin.core.component.inject


class GetCategoryUseCase : BaseUseCase() {
    private val repository by inject<CategoryRepository>()

    suspend fun get(id: Long): Result {
        return try {
            Result.Success(repository.getCategory(id))
        } catch (ex: Exception) {
            Result.Error(ex.localizedMessage ?: ex.message ?: "")
        }
    }
}