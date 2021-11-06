package com.amalkina.beautydiary.ui.common.ext

import java.text.SimpleDateFormat
import java.util.*

private const val DATE_FORMAT = "dd/MM/yyyy"

fun Long.toDate(): String {
    val formatter = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
    return formatter.format(Date(this))
}

fun Long.toStartOfDay() = this.toDate().toTimestamp()

private fun String.toTimestamp(): Long {
    val date = SimpleDateFormat(DATE_FORMAT, Locale.getDefault()).parse(this)
    return date?.time ?: 0
}
