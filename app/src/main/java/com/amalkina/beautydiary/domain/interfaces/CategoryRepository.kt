package com.amalkina.beautydiary.domain.interfaces

import com.amalkina.beautydiary.domain.models.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {

    fun getCategory(id: Long): Flow<Category>

    fun getAllCategories(): Flow<List<Category>>

    suspend fun createCategory(category: Category)

    suspend fun updateCategory(category: Category)

    suspend fun deleteCategory(category: Category)
}