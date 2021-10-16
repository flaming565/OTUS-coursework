package com.amalkina.beautydiary.domain.usecases

import com.amalkina.beautydiary.domain.interfaces.CategoryRepository
import com.amalkina.beautydiary.domain.models.Category
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.inject

@KoinApiExtension
class UpdateCategoryUseCase : BaseUseCase() {
    private val repository by inject<CategoryRepository>()

    suspend fun create(category: Category): Result {
        return try {
            repository.createCategory(category)
            Result.Ok
        } catch (ex: Exception) {
            Result.Error(ex.localizedMessage ?: ex.message ?: "")
        }
    }

    suspend fun update(category: Category): Result {
        return try {
            repository.updateCategory(category)
            Result.Ok
        } catch (ex: Exception) {
            Result.Error(ex.localizedMessage ?: ex.message ?: "")
        }
    }

    suspend fun delete(category: Category): Result {
        return try {
            repository.deleteCategory(category)
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