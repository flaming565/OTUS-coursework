package com.beautydiary.core_ui.ext

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

private const val DATE_FORMAT = "dd/MM/yyyy"
private const val MONTH_DATE_FORMAT = "MM/yyyy"

fun Long.toDate(): String {
    val formatter = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
    return formatter.format(Date(this))
}

fun Long.toMonthDate(): String {
    val formatter = SimpleDateFormat(MONTH_DATE_FORMAT, Locale.getDefault())
    return formatter.format(Date(this))
}

fun Long.toStartOfDay() = this.toDate().toTimestamp()

fun Long.toEndOfDay() = this.toStartOfDay() + TimeUnit.DAYS.toMillis(1) - 1

private fun String.toTimestamp(): Long {
    val date = SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).parse(this)
    return date?.time ?: 0
}
