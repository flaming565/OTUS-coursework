package com.beautydiary.data.models

import androidx.room.Embedded
import androidx.room.Relation


internal data class CategoryWithTasks(
    @Embedded val category: CategoryEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "category_id"
    )
    val tasks: List<TaskEntity>
)
