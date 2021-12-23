package com.beautydiary.core.data.common

import com.beautydiary.core.data.models.*
import com.beautydiary.domain.models.*


internal fun BaseCategoryEntity.toDomain() = DomainCategory(
    id = 0,
    baseCategoryId = this.id,
    stringResName = this.stringResName,
    drawableName = this.drawableName
)

internal fun BaseTaskEntity.toDomain() = DomainTask(
    id = 0,
    categoryId = this.categoryId,
    stringResName = this.stringResName,
    priority = Priority.fromInt(this.priority),
    schedule = this.schedule.toDomain()
)

internal fun CategoryEntity.toDomain() = DomainCategory(
    id = this.id,
    baseCategoryId = this.baseCategoryId,
    name = this.name,
    imagePath = this.imagePath,
    drawableName = this.drawableName,
    updateDate = this.updateDate,
    creationDate = this.creationDate
)

internal fun TaskEntity.toDomain() = DomainTask(
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

internal fun TaskAndCategory.toDomain() = DomainTaskAndCategory(
    task = this.task.toDomain(),
    category = this.category.toDomain()
)

internal fun CategoryWithTasks.toDomain() = DomainCategoryWithTasks(
    category = this.category.toDomain(),
    tasks = this.tasks.map { it.toDomain() }
)

internal fun TaskSchedule.toDomain() = Schedule(
    value = this.value,
    frequency = Frequency.values()[this.frequency.ordinal]
)

internal fun DomainCategory.toEntity() = CategoryEntity(
    id = this.id,
    baseCategoryId = this.baseCategoryId,
    name = this.name,
    imagePath = this.imagePath,
    drawableName = this.drawableName,
    creationDate = this.creationDate
)

internal fun DomainTask.toEntity() = TaskEntity(
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

internal fun Schedule.toEntity() = TaskSchedule(
    value = this.value,
    frequency = TaskFrequency.values()[this.frequency.ordinal]
)

