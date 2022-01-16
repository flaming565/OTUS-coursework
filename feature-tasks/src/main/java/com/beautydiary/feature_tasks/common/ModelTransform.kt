package com.beautydiary.feature_tasks.common

import com.beautydiary.domain.models.DomainTask
import com.beautydiary.domain.models.Priority
import com.beautydiary.feature_tasks.models.CategoryTask

internal fun DomainTask.toUIModel() = CategoryTask(
    id = this.id,
    categoryId = this.categoryId,
    name = this.name,
    stringResName = this.stringResName,
    priority = this.priority.value,
    schedule = this.schedule,
    progress = this.progress,
    daysRemaining = this.daysRemaining,
    note = this.note,
    startDate = this.startDate,
    lastExecutionDate = this.lastExecutionDate
)

fun CategoryTask.toDomain() = DomainTask(
    id = this.id,
    categoryId = this.categoryId,
    name = this.name,
    stringResName = this.stringResName,
    priority = Priority.fromInt(this.priority),
    schedule = this.schedule,
    note = this.note,
    startDate = this.startDate
)