package com.amalkina.beautydiary.ui.common.ext

import com.amalkina.beautydiary.domain.models.DomainCategory
import com.amalkina.beautydiary.domain.models.DomainCategoryWithTasks
import com.amalkina.beautydiary.domain.models.DomainTask
import com.amalkina.beautydiary.ui.common.models.BaseModel.Companion.getContext
import com.amalkina.beautydiary.ui.home.models.HomeCategory
import com.amalkina.beautydiary.ui.tasks.models.CategoryTask

internal fun HomeCategory.toDomain() = DomainCategory(
    id = id,
    baseCategoryId = baseCategoryId,
    name = name,
    imagePath = imagePath,
    drawableName = imageDrawable?.let { getContext().resNameById(it) }
)

internal fun DomainCategory.toUIModel() = HomeCategory(
    id = this.id,
    baseCategoryId = this.baseCategoryId,
    name = this.name,
    stringResName = this.stringResName,
    imagePath = this.imagePath,
    drawableName = this.drawableName
)

internal fun DomainCategoryWithTasks.toUIModel() = HomeCategory(
    id = this.category.id,
    baseCategoryId = this.category.baseCategoryId,
    name = this.category.name,
    stringResName = this.category.stringResName,
    imagePath = this.category.imagePath,
    drawableName = this.category.drawableName,
    progress = this.progress
)

internal fun DomainTask.toUIModel() = CategoryTask(
    id = this.id,
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