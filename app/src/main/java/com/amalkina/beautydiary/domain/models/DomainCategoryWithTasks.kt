package com.amalkina.beautydiary.domain.models

internal data class DomainCategoryWithTasks(
    val category: DomainCategory,
    val tasks: List<DomainTask>
) {
    private val progressOfTasks = tasks.fold(0) { acc, task -> acc + task.progress }
    val progress = if (tasks.isNotEmpty()) progressOfTasks / tasks.size else 0
}