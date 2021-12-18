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
import com.amalkina.beautydiary.domain.models.DomainCategoryWithTasks
import com.amalkina.beautydiary.ui.common.ext.toDate
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet


internal class CommonProgressOfCategoriesChart(context: Context, attrs: AttributeSet?) :
    CategoriesBaseLineChart(context, attrs) {

    override fun configureChartExtraOptions() {
        setLineShader()
        legend.isEnabled = false
        axisLeft.apply {
            isEnabled = true
            setDrawLabels(false)
            setDrawGridLines(false)

            addLimitLine(getLimitLine(45f))
            addLimitLine(getLimitLine(65f))
            addLimitLine(getLimitLine(85f))
        }
    }

    override fun createChartData(categories: List<DomainCategoryWithTasks>): List<ILineDataSet> {
        val dataSet = createDataSet(createEntries(categories), "").run {
            configureDataSet(this)
        }
        return listOf(dataSet)
    }

    private fun createEntries(categories: List<DomainCategoryWithTasks>): List<Entry> {
        val data = mutableListOf<Entry>()

        var startDate = getStartDate()
        val endDate = getEndDate()

        while (startDate < endDate) {
            val result = categories.fold(0F) { acc, category ->
                acc + category.calculateDateNegativeProgress(startDate)
            }
            if (categories.isNotEmpty() && result > 0)
                data.add(Entry(startDate.toFloat(), result / categories.size))

            if (startDate.toDate() == endDate.toDate()) {
                startDate -= DateUtils.MINUTE_IN_MILLIS
            }

            startDate += DateUtils.DAY_IN_MILLIS
        }

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