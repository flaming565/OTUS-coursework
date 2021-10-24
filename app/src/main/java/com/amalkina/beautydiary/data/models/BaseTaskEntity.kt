package com.amalkina.beautydiary.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "base_task")
internal data class BaseTaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "category_id")
    val categoryId: Long = 0,
    @ColumnInfo(name = "string_res_name")
    var stringResName: String? = "",
    var priority: Int = 1,
    var schedule: TaskSchedule = TaskSchedule(),
)