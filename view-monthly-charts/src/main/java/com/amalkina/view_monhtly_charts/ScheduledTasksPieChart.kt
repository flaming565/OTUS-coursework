package com.amalkina.view_monhtly_charts

import android.content.Context
import android.util.AttributeSet
import com.beautydiary.domain.models.DomainCategoryWithTasks
import com.beautydiary.core_ui.ext.toEndOfDay
import com.github.mikephil.charting.data.PieEntry
import java.util.concurrent.TimeUnit


class ScheduledTasksPieChart(context: Context, attrs: AttributeSet?) :
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
        centerText = resources.getString(R.string.monthly_chart_scheduled_tasks, countTasks.toInt())
    }
}