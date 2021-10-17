package com.amalkina.beautydiary.data.room.dao

import androidx.room.*
import com.amalkina.beautydiary.data.models.CategoryModel
import com.amalkina.beautydiary.data.models.CategoryWithTasks
import com.amalkina.beautydiary.data.models.TaskAndCategory
import com.amalkina.beautydiary.data.models.TaskModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

@Dao
internal interface TaskDao {
    @Query("SELECT * FROM task")
    fun getAllTasks(): Flow<List<TaskModel>>

    @Query("SELECT * FROM task WHERE id = :id")
    fun getTask(id: Long): Flow<TaskModel>

    @Query("SELECT * FROM task WHERE categoryId = :category_id")
    fun getCategoryTasks(category_id: Long): Flow<List<TaskModel>>

    @Transaction
    @Query("SELECT * FROM task WHERE id = :id")
    fun getTaskAndCategory(id: Long): List<TaskAndCategory>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: TaskModel)

    @Update
    suspend fun update(task: TaskModel)

    @Delete
    suspend fun delete(task: TaskModel)
}