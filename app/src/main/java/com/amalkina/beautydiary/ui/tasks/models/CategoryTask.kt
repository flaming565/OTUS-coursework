package com.amalkina.beautydiary.ui.tasks.models

import com.amalkina.beautydiary.R
import com.amalkina.beautydiary.domain.models.DomainTask.Companion.DEFAULT_PROGRESS
import com.amalkina.beautydiary.domain.models.Schedule
import com.amalkina.beautydiary.ui.common.ext.resIdByName
import com.amalkina.beautydiary.ui.common.models.BaseModel

internal open class TaskItem(
    open val id: Long = 0,
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
    val daysRemaining: Int = 0,
    val note: String? = null,
    var startDate: Long = System.currentTimeMillis(),
    var lastExecutionDate: Long = startDate
) : TaskItem(name = name) {

    init {
        stringResName?.let {
            name = getString(getContext().resIdByName(it, "string"))
        }
    }

    val progressDesc = "осталось $daysRemaining days"
}

internal object CategoryTaskNew : TaskItem(name = getString(R.string.task_list_new_task))