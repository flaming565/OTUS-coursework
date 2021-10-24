package com.amalkina.beautydiary.domain.models

import java.util.concurrent.TimeUnit


internal data class DomainTask(
    val id: Long = -1,
    val categoryId: Long = -1,
    val name: String = "",
    val stringResName: String? = null,
    var priority: Priority = Priority.LOW,
    var schedule: Schedule = Schedule(),
    val note: String = "",
    var startDate: Long = System.currentTimeMillis(),
    var lastExecutionDate: Long = startDate,
    var updateDate: Long = System.currentTimeMillis(),
    val creationDate: Long = System.currentTimeMillis()
) {
    private val daysAfterExecution = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - lastExecutionDate)
    private val maxAvailableDays = schedule.value * schedule.frequency.value
    val progress = (daysAfterExecution * 100 / maxAvailableDays).toInt()
    val daysRemaining = (maxAvailableDays - daysAfterExecution).toInt()
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
)

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