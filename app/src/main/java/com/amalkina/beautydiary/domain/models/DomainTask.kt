package com.amalkina.beautydiary.domain.models

import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt


internal data class DomainTask(
    val id: Long = -1,
    val categoryId: Long = -1,
    val name: String = "",
    val stringResName: String? = null,
    var priority: Priority = Priority.LOW,
    var schedule: Schedule = Schedule(),
    val note: String? = null,
    var startDate: Long = System.currentTimeMillis(),
    var executionDateList: MutableList<Long> = mutableListOf(startDate),
    var updateDate: Long = System.currentTimeMillis(),
    val creationDate: Long = System.currentTimeMillis()
) {
    val lastExecutionDate = executionDateList.last()
    private val daysAfterExecution =
        TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - lastExecutionDate).toInt()
    private val maxAvailableDays = schedule.getDaysCount()

    private val isBaseTask = stringResName != null
    val progress = if (isBaseTask) DEFAULT_PROGRESS
    else (daysAfterExecution * MAX_PROGRESS.toFloat() / maxAvailableDays)
        .roundToInt()
        .coerceAtLeast(MIN_PROGRESS)

    val daysRemaining = maxAvailableDays - daysAfterExecution

    companion object {
        const val MIN_PROGRESS = 5
        const val MAX_PROGRESS = 100
        const val DEFAULT_PROGRESS = 50

        fun calculateStartDate(progress: Int, schedule: Schedule): Long {
            val maxAvailableDays = schedule.getDaysCount()
            val daysAfterExecution = maxAvailableDays - (progress * maxAvailableDays / MAX_PROGRESS.toFloat())

            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DATE, -daysAfterExecution.roundToInt())
            return calendar.timeInMillis
        }
    }
}

internal enum class Priority(val value: Int) {
    LOW(1),
    MEDIUM(2),
    HIGH(3);

    companion object {
        private val map = values().associateBy(Priority::value)
        fun fromInt(type: Int) = map[type] ?: LOW
    }
}

internal data class Schedule(
    val value: Int = 1,
    val frequency: Frequency = Frequency.DAY
) {
    fun getDaysCount(): Int {
        return value * frequency.value
    }
}

internal enum class Frequency(val value: Int) {
    DAY(1),
    WEEK(7),
    MONTH(30),
    YEAR(365);

    fun next() =
        if (this.ordinal == values().size - 1) this
        else values()[this.ordinal + 1]

    fun previous() =
        if (this.ordinal == 0) this
        else values()[this.ordinal - 1]
}