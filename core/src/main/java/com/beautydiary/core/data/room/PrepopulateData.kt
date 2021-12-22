package com.beautydiary.core.data.room

import android.content.Context
import com.beautydiary.core.R
import com.beautydiary.core.data.models.BaseCategoryEntity
import com.beautydiary.core.data.models.BaseTaskEntity
import com.beautydiary.core.data.models.TaskFrequency
import com.beautydiary.core.data.models.TaskSchedule
import com.beautydiary.core.ui.common.ext.resNameById
import org.koin.core.component.KoinComponent
import org.koin.core.component.get


internal class PrepopulateData : KoinComponent {

    fun getPrepopulateCategory(category: BaseCategoryEnum): BaseCategoryEntity {
        return getRawBaseCategoryData(category).let {
            BaseCategoryEntity(
                stringResName = get<Context>().resNameById(it.first),
                drawableName = get<Context>().resNameById(it.second)
            )
        }
    }

    fun getPrepopulateTasks(category: BaseCategoryEnum, categoryId: Long): List<BaseTaskEntity> {
        return getRawBaseTaskData(category).let { list ->
            list.map {
                BaseTaskEntity(
                    categoryId = categoryId,
                    stringResName = get<Context>().resNameById(it.first),
                    priority = it.second,
                    schedule = it.third
                )
            }
        }
    }

    private fun getRawBaseCategoryData(category: BaseCategoryEnum): Pair<Int, Int> {
        return when (category) {
            BaseCategoryEnum.FACE -> R.string.base_category_face to R.drawable.common_category_face
            BaseCategoryEnum.HANDS -> R.string.base_category_hands to R.drawable.common_category_hands
            BaseCategoryEnum.HAIR -> R.string.base_category_hair to R.drawable.common_category_hair
            BaseCategoryEnum.BODY -> R.string.base_category_body to R.drawable.common_category_body
            BaseCategoryEnum.FEET -> R.string.base_category_legs to R.drawable.common_category_legs
            BaseCategoryEnum.ORAL -> R.string.base_category_mouth to R.drawable.common_category_mouth
            BaseCategoryEnum.OTHER -> R.string.base_category_other to R.drawable.common_category_other
        }
    }

    // TODO: add more tasks
    private fun getRawBaseTaskData(category: BaseCategoryEnum): List<Triple<Int, Int, TaskSchedule>> {
        return when (category) {
            BaseCategoryEnum.FACE -> listOf(
                Triple(
                    R.string.base_face_category_task_day_cream,
                    2,
                    TaskSchedule(1, TaskFrequency.DAY)
                ),
                Triple(
                    R.string.base_face_category_task_night_cream,
                    2,
                    TaskSchedule(1, TaskFrequency.DAY)
                ),
                Triple(
                    R.string.base_face_category_task_serum,
                    2,
                    TaskSchedule(4, TaskFrequency.DAY)
                ),
            )
            BaseCategoryEnum.HANDS -> listOf(
                Triple(
                    R.string.base_hands_category_task_cream,
                    1,
                    TaskSchedule(1, TaskFrequency.DAY)
                ),
                Triple(
                    R.string.base_hands_category_task_massage,
                    1,
                    TaskSchedule(2, TaskFrequency.WEEK)
                ),
                Triple(
                    R.string.base_hands_category_task_manicure,
                    2,
                    TaskSchedule(1, TaskFrequency.MONTH)
                )
            )
            BaseCategoryEnum.HAIR -> listOf(
                Triple(
                    R.string.base_hair_category_task_other,
                    1,
                    TaskSchedule(1, TaskFrequency.WEEK)
                )
            )
            BaseCategoryEnum.BODY -> listOf(
                Triple(
                    R.string.base_body_category_task_cream,
                    1,
                    TaskSchedule(3, TaskFrequency.WEEK)
                ),
                Triple(
                    R.string.base_body_category_task_massage,
                    1,
                    TaskSchedule(4, TaskFrequency.WEEK)
                )
            )
            BaseCategoryEnum.FEET -> listOf(
                Triple(
                    R.string.base_feet_category_task_other,
                    1,
                    TaskSchedule(2, TaskFrequency.WEEK)
                )
            )
            BaseCategoryEnum.ORAL -> listOf(
                Triple(
                    R.string.base_oral_category_task_dentist,
                    3,
                    TaskSchedule(2, TaskFrequency.YEAR)
                ),
                Triple(
                    R.string.base_oral_category_task_other,
                    1,
                    TaskSchedule(2, TaskFrequency.WEEK)
                )
            )
            BaseCategoryEnum.OTHER -> listOf(
                Triple(
                    R.string.base_other_category_task_other,
                    1,
                    TaskSchedule(2, TaskFrequency.WEEK)
                )
            )
        }
    }

    enum class BaseCategoryEnum {
        FACE, HANDS, HAIR, BODY, FEET, ORAL, OTHER
    }
}
