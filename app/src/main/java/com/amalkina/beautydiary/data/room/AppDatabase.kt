package com.amalkina.beautydiary.data.room

import android.content.Context
import androidx.room.RoomDatabase

import androidx.room.Database
import androidx.room.Room
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.amalkina.beautydiary.data.converters.TaskScheduleConverter
import com.amalkina.beautydiary.data.models.BaseCategoryEntity
import com.amalkina.beautydiary.data.models.BaseTaskEntity
import com.amalkina.beautydiary.data.models.CategoryEntity
import com.amalkina.beautydiary.data.models.TaskEntity
import com.amalkina.beautydiary.data.room.dao.BaseCategoryDao
import com.amalkina.beautydiary.data.room.dao.BaseTaskDao
import com.amalkina.beautydiary.data.room.dao.CategoryDao
import com.amalkina.beautydiary.data.room.dao.TaskDao
import java.util.concurrent.Executors


@Database(
    entities = [BaseCategoryEntity::class, BaseTaskEntity::class, CategoryEntity::class, TaskEntity::class],
    version = 6,
    exportSchema = false
)
@TypeConverters(TaskScheduleConverter::class)
internal abstract class AppDatabase : RoomDatabase() {
    abstract fun baseCategoryDao(): BaseCategoryDao
    abstract fun baseTaskDao(): BaseTaskDao
    abstract fun categoryDao(): CategoryDao
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context, AppDatabase::class.java, "db_care_diary")
                .fallbackToDestructiveMigration()
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        val prepopulate = PrepopulateData()
                        val categoryDao = getInstance(context).baseCategoryDao()
                        val taskDao = getInstance(context).baseTaskDao()
                        Executors.newSingleThreadExecutor().execute {
                            PrepopulateData.BaseCategoryEnum.values().forEach {
                                val categoryId = categoryDao.insert(prepopulate.getPrepopulateCategory(it))
                                taskDao.insertAll(prepopulate.getPrepopulateTasks(it, categoryId))
                            }
                        }
                    }
                })
                .build()
    }
}