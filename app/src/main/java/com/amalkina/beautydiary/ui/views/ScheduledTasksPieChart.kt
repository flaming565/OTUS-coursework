package com.amalkina.beautydiary.ui.views

import android.content.Context
import android.util.AttributeSet
import com.amalkina.beautydiary.R
import com.amalkina.beautydiary.domain.models.DomainCategoryWithTasks
import com.amalkina.beautydiary.ui.common.ext.toEndOfDay
import com.github.mikephil.charting.data.PieEntry
import java.util.concurrent.TimeUnit


internal class ScheduledTasksPieChart(context: Context, attrs: AttributeSet?) :
    TasksBasePieChart(context, attrs) {

    override fun createCategoryData(category: DomainCategoryWithTasks): PieEntry? {
        val startDate = getStartDate()
        val endDate = getEndDate()

        var countTasks = 0
        category.tasks
            .filter { it.creationDate < endDate }
            .forEach { task ->
                val taskSchedule = task.schedule.run { value * frequency.value }
                var lastExecutionDate = task.executionDateList.findLast { it < startDate }
                if (lastExecutionDate == null || lastExecutionDate < startDate) {
                    countTasks++
                    lastExecutionDate = startDate.coerceAtLeast(task.creationDate.toEndOfDay())
                }

                lastExecutionDate += TimeUnit.DAYS.toMillis(taskSchedule.toLong())
                while (lastExecutionDate < endDate) {
                    countTasks++
                    lastExecutionDate += TimeUnit.DAYS.toMillis(taskSchedule.toLong())
                }
            }

        return if (countTasks > 0)
            PieEntry(countTasks.toFloat(), category.category.name)
        else null
    }

    override fun setCenterText(data: List<PieEntry>) {
        val countTasks = data.fold(0F) { acc, entry -> acc + entry.value }
        centerText = resources.getString(R.string.statistics_scheduled_tasks, countTasks.toInt())
    }
}