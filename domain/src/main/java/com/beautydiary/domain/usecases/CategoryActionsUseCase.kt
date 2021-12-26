package com.beautydiary.domain.usecases

import com.beautydiary.domain.interfaces.CategoryRepository
import com.beautydiary.domain.models.DomainCategory
import org.koin.core.component.inject


class CategoryActionsUseCase : BaseUseCase() {
    private val repository by inject<CategoryRepository>()

    fun all() =
        flowResult(Unit) { repository.getAllCategories() }

    fun baseCategories() =
        flowResult(Unit) { repository.getBaseCategories() }

    fun getWithTasks(id: Long) =
        flowResult(id) { repository.getCategoryWithTasks(id) }

    fun allWithTasks() =
        flowResult(Unit) { repository.getAllCategoriesWithTasks() }

    suspend fun get(id: Long) =
        suspendResult(id) { repository.getCategory(id) }

    suspend fun create(category: DomainCategory) =
        suspendResult(category) { repository.createCategory(category) }

    suspend fun update(category: DomainCategory) =
        suspendResult(category) { repository.updateCategory(category) }

    suspend fun delete(category: DomainCategory) =
        suspendResult(category) { repository.deleteCategory(category) }
}