package com.amalkina.beautydiary.data.room.dao

import androidx.room.*
import com.amalkina.beautydiary.data.models.CategoryEntity
import com.amalkina.beautydiary.data.models.CategoryWithTasks
import kotlinx.coroutines.flow.Flow

@Dao
internal interface CategoryDao : BaseDao<CategoryEntity> {
    @Query("SELECT * FROM category")
    fun all(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM category WHERE id = :id")
    suspend fun getById(id: Long): CategoryEntity

    @Transaction
    @Query("SELECT * FROM category")
    fun allWithTasks(): Flow<List<CategoryWithTasks>>

    @Transaction
    @Query("SELECT * FROM category WHERE id = :id")
    fun getByIdWithTasks(id: Long): Flow<CategoryWithTasks>
}