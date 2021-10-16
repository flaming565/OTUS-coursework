package com.amalkina.beautydiary.domain.models

data class Task(val id: Long = -1)

data class Frequency(
    val value: Int = 1,
    val type: FrequencyType = FrequencyType.DAY
)

enum class FrequencyType {
    DAY, WEEK, MONTH, YEAR;

    fun next() =
        if (this.ordinal == values().size - 1) this
        else values()[this.ordinal + 1]

    fun previous() =
        if (this.ordinal == 0) this
        else values()[this.ordinal - 1]
}