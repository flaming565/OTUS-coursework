package com.amalkina.beautydiary.ui.views

import android.content.Context
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.drawable.GradientDrawable
import android.text.format.DateUtils
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.amalkina.beautydiary.R
import com.amalkina.beautydiary.domain.models.DomainTask
import com.amalkina.beautydiary.ui.common.ext.toDate
import com.amalkina.beautydiary.ui.common.ext.toStartOfDay
import com.amalkina.beautydiary.ui.common.utils.TimestampValueFormatter
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet


internal open class TaskProgressChart(context: Context, attrs: AttributeSet?) :
    LineChart(context, attrs) {

    init {
        setTouchEnabled(true)
        isDragEnabled = true
        isScaleXEnabled = true
        viewPortHandler.setMaximumScaleX(3f)
        isScaleYEnabled = false
        setPinchZoom(false)
        isDoubleTapToZoomEnabled = false

        legend.isEnabled = false
        description.isEnabled = false

        axisRight.isEnabled = false
        axisLeft.apply {
            isEnabled = true
            setDrawLabels(false)
            setDrawAxisLine(false)
            setDrawGridLines(false)
            axisMinimum = 0F
            axisMaximum = 110F

            addLimitLine(getLimitLine(45f))
            addLimitLine(getLimitLine(65f))
            addLimitLine(getLimitLine(85f))
        }

        xAxis.apply {
            setDrawGridLines(false)
            granularity = DateUtils.DAY_IN_MILLIS.toFloat()
            position = XAxis.XAxisPosition.BOTTOM
            valueFormatter = TimestampValueFormatter()
        }
    }

    fun setTask(task: DomainTask) {
        val data = createData(task)
        setData(data)
    }

    private fun getLimitLine(value: Float): LimitLine {
        return LimitLine(value).apply {
            lineWidth = .1F
            lineColor = ContextCompat.getColor(context, R.color.colorOnPrimary)
            enableDashedLine(40F, 20F, 0F)
        }
    }

    private fun createData(task: DomainTask): List<Entry> {
        val data = mutableListOf<Entry>()

        val tomorrowDate = (System.currentTimeMillis() + DateUtils.DAY_IN_MILLIS).toDate()
        var startDate = task.creationDate.toStartOfDay()

        while (startDate.toDate() != tomorrowDate) {
            data.add(Entry(startDate.toFloat(), task.calculateDateProgress(startDate)))
            startDate += DateUtils.DAY_IN_MILLIS
        }
        // adding end of day for longer chart line duration
        startDate -= DateUtils.MINUTE_IN_MILLIS
        data.add(Entry(startDate.toFloat(), task.calculateDateProgress(startDate)))

        return data
    }

    private fun setData(data: List<Entry>) {
        val dataSet = createDataSet(data)
        this.data = LineData(dataSet)
        updateXAxisSize(dataSet.entryCount)
        updateChart()
    }

    private fun updateChart() {
        setLineShader()
        animateX(500)
        invalidate()
    }

    private fun createDataSet(data: List<Entry>): LineDataSet {
        val colors = intArrayOf(
            ContextCompat.getColor(context, R.color.progress_color_max_overlay),
            ContextCompat.getColor(context, R.color.progress_color_normal_overlay),
            ContextCompat.getColor(context, R.color.progress_color_medium_overlay),
            ContextCompat.getColor(context, R.color.progress_color_min_overlay),
            ContextCompat.getColor(context, R.color.progress_color_min_overlay)
        )
        val gradientBackground = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors)

        return LineDataSet(data, "").apply {
            mode = LineDataSet.Mode.CUBIC_BEZIER
            lineWidth = 2f
            fillDrawable = gradientBackground
            setDrawValues(false)
            setDrawCircles(false)
            setDrawFilled(true)
        }
    }

    private fun setLineShader() {
        val colors = intArrayOf(
            ContextCompat.getColor(context, R.color.progress_color_max),
            ContextCompat.getColor(context, R.color.progress_color_normal),
            ContextCompat.getColor(context, R.color.progress_color_medium),
            ContextCompat.getColor(context, R.color.progress_color_min)
        )
        val positions = floatArrayOf(0f, 0.3f, 0.6f, 1f)

        renderer.paintRender.shader =
            LinearGradient(0F, 0F, 0F, height.toFloat(), colors, positions, Shader.TileMode.CLAMP)
        renderer.paintRender.setShadowLayer(5F, 1F, 1F, Color.GRAY)
    }

    private fun updateXAxisSize(dataSize: Int) {
        val maxDate = System.currentTimeMillis() + DateUtils.DAY_IN_MILLIS * dataSize / 2
        xAxis.axisMaximum = maxDate.toFloat()
    }

}