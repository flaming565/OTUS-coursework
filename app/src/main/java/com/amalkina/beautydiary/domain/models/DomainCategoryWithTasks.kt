package com.amalkina.beautydiary.domain.models

import com.amalkina.beautydiary.ui.common.ext.toStartOfDay


internal data class DomainCategoryWithTasks(
    val category: DomainCategory,
    val tasks: List<DomainTask>
) {
    private val progressOfTasks = tasks.fold(0) { acc, task -> acc + task.progress }
    val progress = if (tasks.isNotEmpty())
        (progressOfTasks / tasks.size).coerceAtLeast(MIN_PROGRESS.toInt())
    else 0

    fun calculateDateNegativeProgress(date: Long): Float {
        if (date < category.creationDate.toStartOfDay())
            return 0F

        val progressOfTasks = tasks.fold(0F) { acc, task -> acc + task.calculateDateProgress(date) }
        return if (tasks.isNotEmpty() && progressOfTasks > 0)
            (progressOfTasks / tasks.size).coerceAtLeast(MIN_PROGRESS)
        else 0F
    }

    fun calculateDateProgress(date: Long): Float {
        return when (val result = calculateDateNegativeProgress(date)) {
            0F -> 0F
            MAX_PROGRESS -> MIN_PROGRESS
            else -> MAX_PROGRESS - result
        }
    }

    companion object {
        const val MIN_PROGRESS = 5F
        const val MAX_PROGRESS = 100F
    }
}