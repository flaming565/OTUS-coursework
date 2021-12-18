package com.amalkina.beautydiary.ui.common.utils

import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.*

class TimestampValueFormatter : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        val date = Date(value.toLong())
        val sdf = SimpleDateFormat("dd MMM", Locale.getDefault())
        return sdf.format(date)
    }
}