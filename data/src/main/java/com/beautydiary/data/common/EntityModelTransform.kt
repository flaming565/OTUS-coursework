package com.beautydiary.data.common

import com.beautydiary.domain.models.*


internal fun com.beautydiary.data.models.BaseCategoryEntity.toDomain() = DomainCategory(
    id = 0,
    baseCategoryId = this.id,
    stringResName = this.stringResName,
    drawableName = this.drawableName
)

internal fun com.beautydiary.data.models.BaseTaskEntity.toDomain() = DomainTask(
    id = 0,
    categoryId = this.categoryId,
    stringResName = this.stringResName,
    priority = Priority.fromInt(this.priority),
    schedule = this.schedule.toDomain()
)

internal fun com.beautydiary.data.models.CategoryEntity.toDomain() = DomainCategory(
    id = this.id,
    baseCategoryId = this.baseCategoryId,
    name = this.name,
    imagePath = this.imagePath,
    drawableName = this.drawableName,
    updateDate = this.updateDate,
    creationDate = this.creationDate
)

internal fun com.beautydiary.data.models.TaskEntity.toDomain() = DomainTask(
    id = this.id,
    categoryId = this.categoryId,
    name = this.name,
    priority = Priority.fromInt(this.priority),
    schedule = this.schedule.toDomain(),
    note = this.note,
    startDate = this.startDate,
    executionDateList = this.executionDateList as MutableList<Long>,
    updateDate = this.updateDate,
    creationDate = this.creationDate
)

internal fun com.beautydiary.data.models.TaskAndCategory.toDomain() = DomainTaskAndCategory(
    task = this.task.toDomain(),
    category = this.category.toDomain()
)

internal fun com.beautydiary.data.models.CategoryWithTasks.toDomain() = DomainCategoryWithTasks(
    category = this.category.toDomain(),
    tasks = this.tasks.map { it.toDomain() }
)

internal fun com.beautydiary.data.models.TaskSchedule.toDomain() = Schedule(
    value = this.value,
    frequency = Frequency.values()[this.frequency.ordinal]
)

internal fun DomainCategory.toEntity() = com.beautydiary.data.models.CategoryEntity(
    id = this.id,
    baseCategoryId = this.baseCategoryId,
    name = this.name,
    imagePath = this.imagePath,
    drawableName = this.drawableName,
    creationDate = this.creationDate
)

internal fun DomainTask.toEntity() = com.beautydiary.data.models.TaskEntity(
    id = this.id,
    categoryId = this.categoryId,
    name = this.name,
    priority = this.priority.value,
    schedule = this.schedule.toEntity(),
    note = this.note ?: "",
    startDate = this.startDate,
    executionDateList = this.executionDateList,
    creationDate = this.creationDate
)

internal fun Schedule.toEntity() = com.beautydiary.data.models.TaskSchedule(
    value = this.value,
    frequency = com.beautydiary.data.models.TaskFrequency.values()[this.frequency.ordinal]
)

