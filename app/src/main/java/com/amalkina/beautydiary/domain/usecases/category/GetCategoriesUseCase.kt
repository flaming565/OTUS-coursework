package com.amalkina.beautydiary.domain.usecases.category

import com.amalkina.beautydiary.domain.common.Result
import com.amalkina.beautydiary.domain.interfaces.CategoryRepository
import com.amalkina.beautydiary.domain.usecases.common.BaseUseCase
import org.koin.core.component.inject


class GetCategoriesUseCase : BaseUseCase() {
    private val repository by inject<CategoryRepository>()

    fun get() = try {
        Result.Success(repository.getAllCategories())
    } catch (ex: Exception) {
        Result.Error(ex.localizedMessage ?: ex.message ?: "")
    }
}