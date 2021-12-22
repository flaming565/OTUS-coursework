package com.amalkina.beautydiary.ui.tasks.models

import com.amalkina.beautydiary.R
import com.amalkina.beautydiary.domain.models.DomainTask.Companion.DEFAULT_PROGRESS
import com.amalkina.beautydiary.domain.models.Schedule
import com.amalkina.beautydiary.ui.common.ext.resIdByName
import com.amalkina.beautydiary.ui.common.models.BaseModel
import java.util.*
import kotlin.math.abs


internal sealed class TaskItem(
    open val id: Long = -1,
    open val name: String = ""
) : BaseModel()

internal data class CategoryTask(
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