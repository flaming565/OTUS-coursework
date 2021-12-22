package com.beautydiary.core.ui.views

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import com.beautydiary.core.domain.models.DomainCategoryWithTasks
import com.beautydiary.core.ui.common.ext.toEndOfDay
import com.beautydiary.core.ui.common.ext.toMonthDate
import com.beautydiary.core.ui.common.ext.toStartOfDay
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import java.util.*


abstract class TasksBasePieChart(context: Context, attrs: AttributeSet?) :
    PieChart(context, attrs) {

    private val now: Calendar = Calendar.getInstance()
    private val chartDate: Calendar = Calendar.getInstance()
    private var categories: List<DomainCategoryWithTasks>? = null

    fun setCategories(categories: List<DomainCategoryWithTasks>) {
        this.categories = categories
        updateData()
    }

    fun setDate(date: Long) {
        chartDate.timeInMillis = date
        updateData()
    }

    abstract fun createCategoryData(category: DomainCategoryWithTasks): PieEntry?

    abstract fun setCenterText(data: List<PieEntry>)

    protected fun getStartDate(): Long {
        chartDate.set(Calendar.DAY_OF_MONTH, 1)
        return chartDate.timeInMillis.toStartOfDay()
    }

    protected fun getEndDate(): Long {
        if (now.timeInMillis.toMonthDate() == chartDate.timeInMillis.toMonthDate())
            chartDate.timeInMillis = now.timeInMillis
        else
            chartDate.set(Calendar.DAY_OF_MONTH, chartDate.getActualMaximum(Calendar.DAY_OF_MONTH))
        return chartDate.timeInMillis.toEndOfDay()
    }

    private fun updateData() {
        this.categories?.let {
            updateChartData(createChartData(it))
            animateX(500)
        }
    }

    private fun updateChartData(data: PieDataSet) {
        this.data = PieData(data)
        configureChart()
        invalidate()
    }

    private fun createChartData(categories: List<DomainCategoryWithTasks>): PieDataSet {
        val data = mutableListOf<PieEntry>()
        val colors = mutableListOf<Int>()

        categories.forEach { category ->
            createCategoryData(category)?.let {
                data.add(it)
                colors.add(category.category.color)
            }
        }
        setCenterText(data)

        return createDataSet(data, colors)
    }

    private fun createDataSet(data: List<PieEntry>, colors: List<Int>): PieDataSet {
        return if (data.isEmpty())
            createEmptyDataSet()
        else PieDataSet(data, "").apply {
            this.colors = colors
            configureLabels()
        }
    }

    private fun createEmptyDataSet(): PieDataSet {
        return PieDataSet(
            listOf(PieEntry(1F, "")), ""
        ).apply {
            this.color = Color.LTGRAY
            setDrawValues(false)
        }
    }

    private fun PieDataSet.configureLabels(): PieDataSet {
        valueTextSize = 8f
        yValuePosition = PieDataSet.ValuePosition.INSIDE_SLICE
        valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return value.toInt().toString()
            }
        }
        setDrawValues(true)

        return this
    }

    private fun configureChart() {
        setTouchEnabled(true)
        configureCommonOptions()
        configureCenterText()
    }

    private fun configureCommonOptions() {
        legend.isEnabled = false
        description.isEnabled = false
        setDrawEntryLabels(false)
    }

    private fun configureCenterText() {
        setDrawCenterText(true)
        setCenterTextSize(12f)
        centerTextRadiusPercent = 90f
    }
}