package com.amalkina.beautydiary.data.room.dao

import androidx.room.*
import com.amalkina.beautydiary.data.models.CategoryModel
import com.amalkina.beautydiary.data.models.CategoryWithTasks
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

@Dao
internal interface CategoryDao {
    @Query("SELECT * FROM category")
    fun getAllCategories(): Flow<List<CategoryModel>>

    @Query("SELECT * FROM category WHERE id = :id")
    suspend fun getCategory(id: Long): CategoryModel

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: CategoryModel)

    @Update
    suspend fun update(category: CategoryModel)

    @Delete
    suspend fun delete(category: CategoryModel)

    @Transaction
    @Query("SELECT * FROM category")
    fun getCategoryWithTasks(): List<CategoryWithTasks>
}