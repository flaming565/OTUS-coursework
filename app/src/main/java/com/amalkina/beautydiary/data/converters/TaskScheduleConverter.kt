package com.amalkina.beautydiary.data.converters

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json
import com.amalkina.beautydiary.data.models.TaskSchedule
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString

internal class TaskScheduleConverter {
    @TypeConverter
    fun fromSchedule(schedule: TaskSchedule): String {
        return Json.encodeToString(schedule)
    }

    @TypeConverter
    fun toSchedule(schedule: String): TaskSchedule {
        return Json.decodeFromString(schedule)
    }
}