package com.beautydiary.core.domain.interfaces

import com.beautydiary.core.domain.models.DomainCategory
import com.beautydiary.core.domain.models.DomainCategoryWithTasks
import kotlinx.coroutines.flow.Flow

internal interface CategoryRepository {

    suspend fun getCategory(id: Long): DomainCategory

    fun getCategoryWithTasks(id: Long): Flow<DomainCategoryWithTasks>

    fun getAllCategories(): Flow<List<DomainCategory>>

    fun getAllCategoriesWithTasks(): Flow<List<DomainCategoryWithTasks>>

    fun getBaseCategories(): Flow<List<DomainCategory>>

    suspend fun createCategory(category: DomainCategory)

    suspend fun updateCategory(category: DomainCategory)

    suspend fun deleteCategory(category: DomainCategory)
}