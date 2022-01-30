package com.beautydiary.data.room.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.beautydiary.data.models.TaskAndCategory
import com.beautydiary.data.models.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface TaskDao : BaseDao<TaskEntity> {
    @Query("SELECT * FROM task")
    fun all(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM task")
    fun allSync(): List<TaskEntity>

    @Query("SELECT * FROM task WHERE id = :id")
    suspend fun getById(id: Long): TaskEntity

    @Transaction
    @Query("SELECT * FROM task WHERE id = :id")
    fun getByIdWithCategory(id: Long): Flow<TaskAndCategory>
}