package com.amalkina.beautydiary.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "task")
internal data class TaskModel(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val categoryId: Long,
    var name: String,
    var priority: Int,
    var schedule: TaskSchedule = TaskSchedule(),
    var note: String = "",
    @ColumnInfo(name = "start_date")
    var startDate: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "update_date")
    var updateDate: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "creation_date")
    val creationDate: Long = System.currentTimeMillis()
)

/**
 * Execute every ${value} ${type}
 */
@Serializable
internal data class TaskSchedule(
    val value: Int = 1,
    val frequency: TaskFrequency = TaskFrequency.DAY
)

@Serializable
internal enum class TaskFrequency { DAY, WEEK, MONTH, YEAR }