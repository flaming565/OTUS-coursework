package com.amalkina.beautydiary.ui.tasks.models

import com.amalkina.beautydiary.R
import com.amalkina.beautydiary.domain.models.Schedule
import com.amalkina.beautydiary.ui.common.ext.resIdByName
import com.amalkina.beautydiary.ui.common.models.BaseModel


internal open class CategoryTask(
    val id: Long = 0,
    var name: String = "",
    val stringResName: String? = null,
    val priority: Int = 1,
    val schedule: Schedule = Schedule(),
    val progress: Int = DEFAULT_PROGRESS,
    val daysRemaining: Int = 0,
    val note: String? = null,
    var startDate: Long = System.currentTimeMillis(),
    var lastExecutionDate: Long = startDate
) : BaseModel() {

    init {
        stringResName?.let {
            name = getString(getContext().resIdByName(it, "string"))
        }
    }

    val progressDesc = "осталось $daysRemaining days"

    companion object {
        const val DEFAULT_PROGRESS = 50
    }
}

internal object CategoryTaskNew : CategoryTask(name = getString(R.string.task_list_new_task))