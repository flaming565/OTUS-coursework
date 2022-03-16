package com.beautydiary.core_view

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
    override fun getFormattedValue(value: Float): String {
        val date = Date(value.toLong())
        val sdf = SimpleDateFormat("hh:mm", Locale.getDefault())
        return sdf.format(date)
    }
}