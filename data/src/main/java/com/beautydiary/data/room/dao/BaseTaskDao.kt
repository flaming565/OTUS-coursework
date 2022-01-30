package com.beautydiary.data.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.beautydiary.data.models.BaseTaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface BaseTaskDao {
    @Query("SELECT * FROM base_task")
    fun getAll(): Flow<List<BaseTaskEntity>>

    @Query("SELECT * FROM base_task WHERE category_id = :categoryId")
    fun getCategoryTasks(categoryId: Long): Flow<List<BaseTaskEntity>>

    @Query("SELECT * FROM base_task WHERE id = :id")
    suspend fun getById(id: Long): BaseTaskEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(category: BaseTaskEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(data: List<BaseTaskEntity>)
}