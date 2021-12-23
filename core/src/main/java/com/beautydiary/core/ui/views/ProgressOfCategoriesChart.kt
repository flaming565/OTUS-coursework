package com.beautydiary.core.ui.views

import android.content.Context
import android.text.format.DateUtils
import android.util.AttributeSet
import com.beautydiary.domain.models.DomainCategoryWithTasks
import com.beautydiary.core.ui.common.ext.toDate
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet


class ProgressOfCategoriesChart(context: Context, attrs: AttributeSet?) :
    CategoriesBaseLineChart(context, attrs) {

    override fun configureChartExtraOptions() {
        setDrawGridBackground(false)

        extraBottomOffset = 20f

        legend.apply {
            isEnabled = true
            horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
            isWordWrapEnabled = true
        }

        axisLeft.apply {
            setDrawLabels(true)
            setDrawGridLines(true)
        }
    }

    override fun createChartData(categories: List<DomainCategoryWithTasks>): List<ILineDataSet> {
        val data = mutableListOf<ILineDataSet>()

        categories.forEach { category ->
            val categoryData = createCategoryData(category)
            val dataSet = createDataSet(categoryData, category.category.name).apply {
                color = category.category.color
                setDrawFilled(false)
            }
            data.add(dataSet)
        }

        return data
    }

    private fun createCategoryData(category: DomainCategoryWithTasks): List<Entry> {
        val data = mutableListOf<Entry>()

        var startDate = getStartDate()
        val endDate = getEndDate()

        while (startDate < endDate) {
            val categoryProgress = category.calculateDateProgress(startDate)
            if (categoryProgress > 0)
                data.add(Entry(startDate.toFloat(), categoryProgress))

            if (startDate.toDate() == endDate.toDate()) {
                startDate -= DateUtils.MINUTE_IN_MILLIS
            }

            startDate += DateUtils.DAY_IN_MILLIS
        }

        return if (data.size > 0) data else emptyList()
    }

}