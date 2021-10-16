package com.amalkina.beautydiary.data.room

import android.content.Context
import androidx.room.RoomDatabase

import androidx.room.Database
import androidx.room.Room
import com.amalkina.beautydiary.data.models.CategoryModel
import com.amalkina.beautydiary.data.room.dao.CategoryDao


@Database(
    entities = [CategoryModel::class],
    version = 3,
    exportSchema = false
)
internal abstract class AppDatabase : RoomDatabase() {
    abstract fun categoryDao(): CategoryDao

    companion object {
        fun buildDatabase(context: Context) =
            Room.databaseBuilder(context, AppDatabase::class.java, "db_care_diary")
                .fallbackToDestructiveMigration()
                .build()
    }
}