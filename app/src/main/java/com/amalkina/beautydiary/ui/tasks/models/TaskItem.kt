package com.amalkina.beautydiary.ui.tasks.models

import com.amalkina.beautydiary.domain.models.Schedule

sealed class TaskItem {

    object New : TaskItem()

    data class Task(
        val id: Long,
        val title: String,
        val priority: Int = 1,
        val schedule: Schedule = Schedule(),
        val progress: Int = 50,
        val note: String? = null
    ) : TaskItem() {
        val progressDesc = "осталось ${schedule.value} days"
    }
}