package com.beautydiary.view_task_progress

import android.content.Context
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Shader
import android.graphics.drawable.GradientDrawable
import android.text.format.DateUtils.DAY_IN_MILLIS
import android.text.format.DateUtils.HOUR_IN_MILLIS
import android.util.AttributeSet
import androidx.core.content.ContextCompat
import com.beautydiary.core_ui.ext.toStartOfDay
import com.beautydiary.core_view.R
import com.beautydiary.core_view.DayTimestampFormatter
import com.beautydiary.core_view.HourTimestampFormatter
import com.beautydiary.domain.models.DomainTask
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import java.util.concurrent.TimeUnit


open class TaskProgressChart(context: Context, attrs: AttributeSet?) :
    LineChart(context, attrs) {

    private val now = System.currentTimeMillis()
    private var task: DomainTask? = null

    init {
        animateX(500)
    }

    fun setTask(task: DomainTask) {
        this.task = task
        updateData()
    }

    private fun updateData() {
        this.task?.let {
            updateChartData(createChartData(it))
        }
    }

    private fun updateChartData(data: List<ILineDataSet>) {
        this.data = LineData(data)
        configureChart()
        invalidate()
    }

    private fun createChartData(task: DomainTask): List<ILineDataSet> {
        val dataSet = createDataSet(createEntries(task)).run {
            configureDataSet(this)
        }
        return listOf(dataSet)
    }

    private fun createDataSet(data: List<Entry>): LineDataSet {
        return LineDataSet(data, "").apply {
            lineWidth = 2f
            setDrawValues(false)
            setDrawCircles(false)
            setDrawFilled(true)
        }
    }

    private fun createEntries(task: DomainTask): List<Entry> {
        val data = mutableListOf<Entry>()
        var startDate = task.creationDate
        val dataStep = if (TimeUnit.MILLISECONDS.toDays(now - startDate) > 1)
            DAY_IN_MILLIS else HOUR_IN_MILLIS

        data.add(
            Entry(
                startDate.toStartOfDay().toFloat(),
                task.calculateDateProgress(startDate)
            )
        )
        while (startDate <= now) {
            data.add(Entry(startDate.toFloat(), task.calculateDateProgress(startDate)))
            startDate += dataStep
        }
        data.add(Entry(now.toFloat(), task.calculateDateProgress(now)))

        return data
    }

    private fun configureDataSet(dataSet: LineDataSet): LineDataSet {
        val colors = intArrayOf(
            ContextCompat.getColor(context, R.color.progress_color_max_overlay),
            ContextCompat.getColor(context, R.color.progress_color_normal_overlay),
            ContextCompat.getColor(context, R.color.progress_color_medium_overlay),
            ContextCompat.getColor(context, R.color.progress_color_min_overlay),
            ContextCompat.getColor(context, R.color.progress_color_min_overlay)
        )
        val gradientBackground = GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors)

        return dataSet.apply {
            fillDrawable = gradientBackground
        }
    }

    private fun configureChart() {
        configureCommonOptions()
        configureScaleOptions()
        configureXAxis()
        configureSideAxis()
    }

    private fun configureCommonOptions() {
        extraTopOffset = 20f
        legend.isEnabled = false
        description.isEnabled = false
        setLineShader()
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
            position = XAxis.XAxisPosition.BOTTOM

            task?.let { task ->
                if (TimeUnit.MILLISECONDS.toDays(now - task.startDate) > 1) {
                    granularity = DAY_IN_MILLIS.toFloat()
                    valueFormatter = DayTimestampFormatter()
                } else {
                    granularity = HOUR_IN_MILLIS.toFloat()
                    valueFormatter = HourTimestampFormatter()
                }

                val startDate = task.creationDate.toStartOfDay().toFloat()
                axisMinimum = startDate
                val taskLifetime = (now - startDate)
                axisMaximum = (now + taskLifetime / 2)
            }
            textColor = context.resources.getColor(R.color.colorOnPrimary, context.theme)
        }
    }

    private fun configureSideAxis() {
        axisRight.isEnabled = false

        axisLeft.apply {
            isEnabled = true
            setDrawAxisLine(true)
            setDrawLabels(false)
            setDrawGridLines(false)

            axisMinimum = 0F
            axisMaximum = 110F

            addLimitLine(getLimitLine(45f))
            addLimitLine(getLimitLine(65f))
            addLimitLine(getLimitLine(85f))
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

    private fun getLimitLine(value: Float): LimitLine {
        return LimitLine(value).apply {
            lineWidth = .1F
            lineColor = ContextCompat.getColor(context, R.color.colorOnPrimary)
            enableDashedLine(40F, 20F, 0F)
        }
    }

}