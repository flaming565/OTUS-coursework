package com.beautydiary.core_view

import android.text.format.DateUtils
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.*

class DayTimestampFormatter : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        val date = Date(value.toLong())
        val sdf = SimpleDateFormat("dd MMM", Locale.getDefault())
        return sdf.format(date)
    }
}

class HourTimestampFormatter : ValueFormatter() {
    private fun getDate(value: Long): Date {
        val interval = DateUtils.HOUR_IN_MILLIS / 4
        return Date((value / interval) * interval)
    }

    override fun getFormattedValue(value: Float): String {
        val date = getDate(value.toLong())
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format(date)
    }
}