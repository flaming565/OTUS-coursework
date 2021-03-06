package com.beautydiary.feature_tasks.models

import com.beautydiary.feature_tasks.R
import com.beautydiary.domain.models.DomainTask.Companion.DEFAULT_PROGRESS
import com.beautydiary.domain.models.Schedule
import com.beautydiary.core_ui.ext.resIdByName
import com.beautydiary.core_ui.models.BaseModel
import java.util.*
import kotlin.math.abs


sealed class TaskItem(
    open val id: Long = -1,
    open val name: String = ""
) : BaseModel()

data class CategoryTask(
    override val id: Long = 0,
    val categoryId: Long = 0,
    override var name: String = "",
    val stringResName: String? = null,
    val priority: Int = 1,
    val schedule: Schedule = Schedule(),
    val progress: Int = DEFAULT_PROGRESS,
    var daysRemaining: Int = 0,
    val note: String? = null,
    var startDate: Long = System.currentTimeMillis(),
    var lastExecutionDate: Long = startDate
) : TaskItem(name = name) {

    init {
        stringResName?.let {
            name = getString(getContext().resIdByName(it, "string"))
        }
    }

    val progressDesc = getPlurals(
        when {
            daysRemaining < 0 -> R.plurals.task_list_days_ago
            else -> R.plurals.task_list_days_left
        }, abs(daysRemaining), abs(daysRemaining)
    )

    val progressDetailDesc = getPlurals(
        when {
            daysRemaining < 0 -> R.plurals.task_detail_complete_days_ago
            else -> R.plurals.task_detail_complete_days_left
        }, abs(daysRemaining), abs(daysRemaining)
    )

    val scheduleDesc =
        if (schedule.value == 1) "${schedule.value} ${schedule.frequency.name.lowercase(Locale.getDefault())}"
        else "${schedule.value} ${schedule.frequency.name.lowercase(Locale.getDefault())}s"
}

internal object CategoryTaskNew : TaskItem(name = getString(R.string.task_list_new_task))