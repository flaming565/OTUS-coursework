package com.beautydiary.core.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "base_category")
internal data class BaseCategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "string_res_name")
    var stringResName: String? = "",
    @ColumnInfo(name = "drawable_name")
    var drawableName: String? = null,
)