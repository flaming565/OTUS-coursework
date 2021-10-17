package com.amalkina.beautydiary.domain.models


data class Task(
    val id: Long = -1,
    val categoryId: Long = -1,
    val name: String = "",
    var priority: Priority = Priority.LOW,
    var schedule: Schedule = Schedule(),
    val note: String = "",
    var startDate: Long = System.currentTimeMillis(),
    var updateDate: Long = System.currentTimeMillis(),
    val creationDate: Long = System.currentTimeMillis()
)

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
)

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