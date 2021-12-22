package com.beautydiary.core.data.room.dao

import androidx.room.*
import com.beautydiary.core.data.models.TaskAndCategory
import com.beautydiary.core.data.models.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface TaskDao : BaseDao<TaskEntity> {
    @Query("SELECT * FROM task")
    fun all(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM task WHERE id = :id")
    suspend fun getById(id: Long): TaskEntity

    @Transaction
    @Query("SELECT * FROM task WHERE id = :id")
    fun getByIdWithCategory(id: Long): Flow<TaskAndCategory>
}