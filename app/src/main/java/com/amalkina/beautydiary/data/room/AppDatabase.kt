package com.amalkina.beautydiary.data.room

import android.content.Context
import androidx.room.RoomDatabase

import androidx.room.Database
import androidx.room.Room
import androidx.room.TypeConverters
import com.amalkina.beautydiary.data.converters.TaskScheduleConverter
import com.amalkina.beautydiary.data.models.CategoryModel
import com.amalkina.beautydiary.data.models.TaskModel
import com.amalkina.beautydiary.data.room.dao.CategoryDao
import com.amalkina.beautydiary.data.room.dao.TaskDao


@Database(
    entities = [CategoryModel::class, TaskModel::class],
    version = 4,
    exportSchema = false
)
@TypeConverters(TaskScheduleConverter::class)
internal abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao
    abstract fun taskDao(): TaskDao

    companion object {
        fun buildDatabase(context: Context) =
            Room.databaseBuilder(context, AppDatabase::class.java, "db_care_diary")
                .fallbackToDestructiveMigration()
                .build()
    }
}