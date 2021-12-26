package com.beautydiary.data.converters

import androidx.room.TypeConverter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class DateListTransformer {
    @TypeConverter
    fun fromDateList(value: List<Long>): String {
        return Json.encodeToString(value)
    }

    @TypeConverter
    fun toDateList(value: String): List<Long> {
        return Json.decodeFromString(value)
    }
}