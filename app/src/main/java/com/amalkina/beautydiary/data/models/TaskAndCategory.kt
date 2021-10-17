package com.amalkina.beautydiary.data.models

import androidx.room.Embedded
import androidx.room.Relation

internal data class TaskAndCategory(
    @Embedded
    val category: CategoryModel,
    @Relation(
        parentColumn = "id",
        entityColumn = "categoryId"
    )
    val task: TaskModel
)
