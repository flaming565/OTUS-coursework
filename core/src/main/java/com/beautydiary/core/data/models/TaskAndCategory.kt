package com.beautydiary.core.data.models

import androidx.room.Embedded
import androidx.room.Relation

internal data class TaskAndCategory(
    @Embedded
    val task: TaskEntity,
    @Relation(
        parentColumn = "category_id",
        entityColumn = "id"
    )
    val category: CategoryEntity
)
