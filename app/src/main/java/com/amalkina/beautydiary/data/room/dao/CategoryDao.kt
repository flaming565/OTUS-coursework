package com.amalkina.beautydiary.data.room.dao

import androidx.room.*
import com.amalkina.beautydiary.data.models.CategoryModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

@Dao
internal interface CategoryDao {
    @Query("SELECT * FROM category")
    fun getAllCategories(): Flow<List<CategoryModel>>

    @Query("SELECT * FROM category WHERE id = :id")
    fun getCategory(id: Long): Flow<CategoryModel>

    fun getCategoryDistinctUntilChanged(id: Long) =
        getCategory(id).distinctUntilChanged()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: CategoryModel)

    @Update
    suspend fun update(category: CategoryModel)

    @Delete
    suspend fun delete(category: CategoryModel)
}