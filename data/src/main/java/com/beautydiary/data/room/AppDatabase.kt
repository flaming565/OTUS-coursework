package com.beautydiary.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.beautydiary.data.converters.DateListTransformer
import com.beautydiary.data.converters.TaskScheduleConverter
import com.beautydiary.data.models.BaseCategoryEntity
import com.beautydiary.data.models.BaseTaskEntity
import com.beautydiary.data.models.CategoryEntity
import com.beautydiary.data.models.TaskEntity
import com.beautydiary.data.room.dao.BaseCategoryDao
import com.beautydiary.data.room.dao.BaseTaskDao
import com.beautydiary.data.room.dao.CategoryDao
import com.beautydiary.data.room.dao.TaskDao
import java.util.concurrent.Executors


@Database(
    entities = [BaseCategoryEntity::class, BaseTaskEntity::class, CategoryEntity::class, TaskEntity::class],
    version = 10,
    exportSchema = false
)
@TypeConverters(TaskScheduleConverter::class, DateListTransformer::class)
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
                buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context, AppDatabase::class.java, "db_care_diary")
                .fallbackToDestructiveMigration()
                .addCallback(object : Callback() {
                    override fun onOpen(db: SupportSQLiteDatabase) {
                        super.onOpen(db)

                        val categoryDao = getInstance(context).baseCategoryDao()
                        val taskDao = getInstance(context).baseTaskDao()

                        Executors.newSingleThreadExecutor().execute {
                            val categories = categoryDao.all()
                            if (categories.isEmpty()) {
                                val prepopulate = PrepopulateData()
                                PrepopulateData.BaseCategoryEnum.values().forEach {
                                    val categoryId =
                                        categoryDao.insert(prepopulate.getPrepopulateCategory(it))
                                    taskDao.insertAll(
                                        prepopulate.getPrepopulateTasks(
                                            it,
                                            categoryId
                                        )
                                    )
                                }
                            }
                        }
                    }
                })
                .build()
    }
}