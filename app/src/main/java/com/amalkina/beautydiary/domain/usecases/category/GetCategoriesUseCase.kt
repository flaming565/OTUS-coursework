package com.amalkina.beautydiary.domain.usecases.category

import com.amalkina.beautydiary.domain.interfaces.CategoryRepository
import com.amalkina.beautydiary.domain.models.Category
import com.amalkina.beautydiary.domain.usecases.common.BaseUseCase
import kotlinx.coroutines.flow.Flow
import org.koin.core.component.inject


class GetCategoriesUseCase : BaseUseCase() {
    private val repository by inject<CategoryRepository>()

    fun get() = try {
        Result.Ok(repository.getAllCategories())
    } catch (ex: Exception) {
        Result.Error(ex.localizedMessage ?: ex.message ?: "")
    }

    sealed class Result {
        data class Ok(val response: Flow<List<Category>>) : Result()
        data class Error(val error: String) : Result()

    }
}