package com.beautydiary.core.ui.views

import android.content.Context
import android.text.format.DateUtils
import android.util.AttributeSet
import com.beautydiary.domain.models.DomainCategoryWithTasks
import com.beautydiary.core_ui.ext.toEndOfDay
import com.beautydiary.core_ui.ext.toMonthDate
import com.beautydiary.core_ui.ext.toStartOfDay
import com.beautydiary.core.ui.common.utils.TimestampValueFormatter
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import java.util.*


abstract class CategoriesBaseLineChart(context: Context, attrs: AttributeSet?) :
    LineChart(context, attrs) {

    private var categories: List<DomainCategoryWithTasks>? = null
    private val now = Calendar.getInstance()
    private val chartDate = Calendar.getInstance()

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

    fun setCategories(categories: List<DomainCategoryWithTasks>) {
        this.categories = categories
        updateData()
    }

    fun setDate(date: Long) {
        chartDate.timeInMillis = date
        updateData()
    }

    private fun updateData() {
        this.categories?.let {
            updateChartData(createChartData(it))
            animateX(500)
        }
    }

    abstract fun createChartData(categories: List<DomainCategoryWithTasks>): List<ILineDataSet>

    fun createDataSet(data: List<Entry>, label: String = ""): LineDataSet {
        return LineDataSet(data, label).apply {
            mode = LineDataSet.Mode.CUBIC_BEZIER
            lineWidth = 2f
            setDrawValues(false)
            setDrawCircles(false)
            setDrawFilled(true)
        }
    }

    private fun updateChartData(data: List<ILineDataSet>) {
        this.data = LineData(data)
        configureChart()
        invalidate()
    }

    private fun configureChart() {
        configureCommonOptions()
        configureScaleOptions()
        configureXAxis()
        configureChartExtraOptions()
    }

    private fun configureCommonOptions() {
        extraTopOffset = 20f
        description.isEnabled = false
        axisRight.isEnabled = false
        axisLeft.apply {
            isEnabled = true
            setDrawAxisLine(true)
            axisMinimum = 0F
            axisMaximum = 105F
        }
    }

    private fun configureScaleOptions() {
        setTouchEnabled(true)
        isDragEnabled = true
        isScaleXEnabled = true
        viewPortHandler.setMaximumScaleX(3f)
        isScaleYEnabled = false
        setPinchZoom(false)
        isDoubleTapToZoomEnabled = false
    }

    private fun configureXAxis() {
        xAxis.apply {
            setDrawGridLines(false)

            granularity = DateUtils.DAY_IN_MILLIS.toFloat()
            position = XAxis.XAxisPosition.BOTTOM
            valueFormatter = TimestampValueFormatter()

            axisMinimum = getStartDate().toFloat()
            chartDate.set(Calendar.DAY_OF_MONTH, chartDate.getActualMaximum(Calendar.DAY_OF_MONTH))
            axisMaximum = chartDate.timeInMillis.toEndOfDay().toFloat()
        }
    }

    abstract fun configureChartExtraOptions()
}