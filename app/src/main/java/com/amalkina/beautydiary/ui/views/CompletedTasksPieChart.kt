package com.amalkina.beautydiary.ui.views

import android.content.Context
import android.util.AttributeSet
import com.amalkina.beautydiary.R
import com.amalkina.beautydiary.domain.models.DomainCategoryWithTasks
import com.github.mikephil.charting.data.PieEntry


internal class CompletedTasksPieChart(context: Context, attrs: AttributeSet?) :
    TasksBasePieChart(context, attrs) {

    override fun createCategoryData(category: DomainCategoryWithTasks): PieEntry? {
        val startDate = getStartDate()
        val endDate = getEndDate()

        val countTasks = category.tasks
            .filter { it.creationDate < endDate }
            .fold(0F) { acc, task ->
                acc + task.userExecutionList.filter { date ->
                    date in startDate..endDate
                }.size
            }

        return if (countTasks > 0)
            PieEntry(countTasks, category.category.name)
        else null
    }

    override fun setCenterText(data: List<PieEntry>) {
        val countTasks = data.fold(0F) { acc, entry -> acc + entry.value }
        centerText = resources.getString(R.string.statistics_completed_tasks, countTasks.toInt())
    }
}