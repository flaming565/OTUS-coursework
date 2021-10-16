package com.amalkina.beautydiary.ui.tasks.models

import com.amalkina.beautydiary.domain.models.Frequency

sealed class TaskItem {

    object New : TaskItem()

    data class Task(
        val id: Long,
        val title: String,
        val priority: Int = 1,
        val frequency: Frequency = Frequency(),
        val progress: Int = 50,
        val note: String? = null
    ) : TaskItem() {
        val progressDesc = "осталось ${frequency.value} days"
    }
}