package com.beautydiary.domain.models

import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt


data class DomainTask(
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
    private val hoursAfterExecution =
        TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis() - lastExecutionDate).toInt()
    private val maxAvailableHours = schedule.getHoursCount()

    private val isBaseTask = stringResName != null
    val progress = calculateProgress()

    val daysRemaining = ((maxAvailableHours - hoursAfterExecution) / 24.0).roundToInt()
    val userExecutionList = executionDateList.drop(1)

    fun calculateDateProgress(date: Long): Float {
        if (date < startDate)
            return -1F

        val lastDate = executionDateList
            .findLast { it <= date } ?: startDate
        val hoursAfterExecution = TimeUnit.MILLISECONDS.toHours(date - lastDate)

        val value = (hoursAfterExecution * MAX_PROGRESS.toFloat() / maxAvailableHours)
        return when {
            value > MAX_PROGRESS -> MAX_PROGRESS.toFloat()
            value < MIN_PROGRESS -> 2f
            else -> value
        }
    }

    private fun calculateProgress(): Int {
        return if (isBaseTask) DEFAULT_PROGRESS
        else {
            val value = (hoursAfterExecution * MAX_PROGRESS.toFloat() / maxAvailableHours)
                .roundToInt()
            when {
                value > MAX_PROGRESS -> MAX_PROGRESS
                value < MIN_PROGRESS -> MIN_PROGRESS
                else -> value
            }
        }
    }

    companion object {
        const val MIN_PROGRESS = 5
        const val MAX_PROGRESS = 100
        const val DEFAULT_PROGRESS = 50

        fun calculateStartDate(progress: Int, schedule: Schedule): Long {
            val maxAvailableHours = schedule.getHoursCount()
            val hoursAfterExecution =
                maxAvailableHours - (progress * maxAvailableHours / MAX_PROGRESS.toFloat())

            val calendar = Calendar.getInstance()
            calendar.add(Calendar.HOUR, -hoursAfterExecution.roundToInt())
            return calendar.timeInMillis
        }
    }
}

enum class Priority(val value: Int) {
    LOW(1),
    MEDIUM(2),
    HIGH(3);

    companion object {
        private val map = values().associateBy(Priority::value)
        fun fromInt(type: Int) = map[type] ?: LOW
    }
}

data class Schedule(
    val value: Int = 1,
    val frequency: Frequency = Frequency.DAY
) {
    fun getDaysCount(): Int {
        return value * frequency.value
    }

    fun getHoursCount(): Int {
        return value * frequency.value * 24
    }
}

enum class Frequency(val value: Int) {
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