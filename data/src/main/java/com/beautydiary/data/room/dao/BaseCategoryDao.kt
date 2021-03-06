package com.beautydiary.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.beautydiary.data.models.BaseCategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface BaseCategoryDao {
    @Query("SELECT * FROM base_category")
    fun getAll(): Flow<List<BaseCategoryEntity>>

    @Query("SELECT * FROM base_category WHERE id = :id")
    suspend fun getById(id: Long): BaseCategoryEntity

    @Query("SELECT * FROM base_category")
    fun all(): List<BaseCategoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(category: BaseCategoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(data: List<BaseCategoryEntity>)
}