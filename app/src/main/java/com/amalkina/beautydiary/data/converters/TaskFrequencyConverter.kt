package com.amalkina.beautydiary.data.converters

import androidx.room.TypeConverter
import com.amalkina.beautydiary.data.models.TaskFrequency

internal class TaskFrequencyConverter {
    @TypeConverter
    fun fromFrequency(frequency: TaskFrequency): String {
        return frequency.name
    }

    @TypeConverter
    fun toFrequency(frequency: String): TaskFrequency {
        return TaskFrequency.valueOf(frequency)
    }
}