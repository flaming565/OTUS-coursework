package com.amalkina.beautydiary.data.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "category")
internal data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "base_category_id")
    val baseCategoryId: Long = 0,
    var name: String,
    @ColumnInfo(name = "image_path")
    var imagePath: String? = null,
    @ColumnInfo(name = "drawable_name")
    var drawableName: String? = null,
    @ColumnInfo(name = "update_date")
    var updateDate: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "creation_date")
    val creationDate: Long = System.currentTimeMillis()
)