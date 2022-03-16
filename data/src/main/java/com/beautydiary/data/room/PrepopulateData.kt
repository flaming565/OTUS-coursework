package com.beautydiary.data.room

import android.content.Context
import com.beautydiary.data.R
import com.beautydiary.data.models.BaseCategoryEntity
import com.beautydiary.data.models.BaseTaskEntity
import com.beautydiary.data.models.TaskFrequency
import com.beautydiary.data.models.TaskSchedule
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

    private fun getRawBaseTaskData(category: BaseCategoryEnum): List<Triple<Int, Int, TaskSchedule>> {
        return when (category) {
            BaseCategoryEnum.FACE -> listOf(
                Triple(
                    R.string.base_face_category_task_toner,
                    2,
                    TaskSchedule(1, TaskFrequency.DAY)
                ),
                Triple(
                    R.string.base_face_category_task_essence,
                    2,
                    TaskSchedule(1, TaskFrequency.DAY)
                ),
                Triple(
                    R.string.base_face_category_task_emulsion,
                    2,
                    TaskSchedule(1, TaskFrequency.DAY)
                ),
                Triple(
                    R.string.base_face_category_task_serum,
                    2,
                    TaskSchedule(1, TaskFrequency.DAY)
                ),
                Triple(
                    R.string.base_face_category_task_patch,
                    2,
                    TaskSchedule(2, TaskFrequency.DAY)
                ),
                Triple(
                    R.string.base_face_category_task_peeling,
                    1,
                    TaskSchedule(1, TaskFrequency.WEEK)
                ),
                Triple(
                    R.string.base_face_category_task_sheet_mask,
                    1,
                    TaskSchedule(4, TaskFrequency.DAY)
                ),
                Triple(
                    R.string.base_face_category_task_clay_mask,
                    1,
                    TaskSchedule(1, TaskFrequency.WEEK)
                ),
                Triple(
                    R.string.base_face_category_task_algin_mask,
                    1,
                    TaskSchedule(1, TaskFrequency.WEEK)
                ),
                Triple(
                    R.string.base_face_category_task_facebuilding,
                    2,
                    TaskSchedule(1, TaskFrequency.DAY)
                ),
                Triple(
                    R.string.base_face_category_task_gua_sha,
                    1,
                    TaskSchedule(3, TaskFrequency.DAY)
                ),
                Triple(
                    R.string.base_category_task_other,
                    1,
                    TaskSchedule(2, TaskFrequency.DAY)
                )
            )
            BaseCategoryEnum.HANDS -> listOf(
                Triple(
                    R.string.base_hands_category_task_cream,
                    1,
                    TaskSchedule(2, TaskFrequency.DAY)
                ),
                Triple(
                    R.string.base_hands_category_task_manicure,
                    2,
                    TaskSchedule(3, TaskFrequency.WEEK)
                ),
                Triple(
                    R.string.base_hands_category_task_massage,
                    1,
                    TaskSchedule(1, TaskFrequency.WEEK)
                ),
                Triple(
                    R.string.base_hands_category_task_oil,
                    1,
                    TaskSchedule(3, TaskFrequency.DAY)
                ),
                Triple(
                    R.string.base_hands_category_task_mask,
                    1,
                    TaskSchedule(1, TaskFrequency.WEEK)
                ),
                Triple(
                    R.string.base_hands_category_task_paraffin,
                    1,
                    TaskSchedule(1, TaskFrequency.WEEK)
                ),
                Triple(
                    R.string.base_hands_category_task_spa,
                    1,
                    TaskSchedule(2, TaskFrequency.WEEK)
                ),
                Triple(
                    R.string.base_category_task_other,
                    1,
                    TaskSchedule(1, TaskFrequency.WEEK)
                )
            )
            BaseCategoryEnum.HAIR -> listOf(
                Triple(
                    R.string.base_hair_category_task_shampoo,
                    2,
                    TaskSchedule(3, TaskFrequency.DAY)
                ),
                Triple(
                    R.string.base_hair_category_task_conditioner,
                    2,
                    TaskSchedule(3, TaskFrequency.DAY)
                ),
                Triple(
                    R.string.base_hair_category_task_mask,
                    1,
                    TaskSchedule(1, TaskFrequency.WEEK)
                ),
                Triple(
                    R.string.base_hair_category_task_scrub,
                    1,
                    TaskSchedule(1, TaskFrequency.WEEK)
                ),
                Triple(
                    R.string.base_hair_category_task_haircut,
                    1,
                    TaskSchedule(2, TaskFrequency.MONTH)
                ),
                Triple(
                    R.string.base_hair_category_task_coloring,
                    1,
                    TaskSchedule(2, TaskFrequency.MONTH)
                ),
                Triple(
                    R.string.base_hair_category_task_ends_therapy,
                    1,
                    TaskSchedule(3, TaskFrequency.DAY)
                ),
                Triple(
                    R.string.base_hair_category_task_mesotherapy,
                    2,
                    TaskSchedule(1, TaskFrequency.MONTH)
                ),
                Triple(
                    R.string.base_category_task_other,
                    1,
                    TaskSchedule(1, TaskFrequency.WEEK)
                )
            )
            BaseCategoryEnum.BODY -> listOf(
                Triple(
                    R.string.base_body_category_task_cream,
                    2,
                    TaskSchedule(3, TaskFrequency.DAY)
                ),
                Triple(
                    R.string.base_body_category_task_scrub,
                    2,
                    TaskSchedule(2, TaskFrequency.WEEK)
                ),
                Triple(
                    R.string.base_body_category_task_massage,
                    2,
                    TaskSchedule(1, TaskFrequency.WEEK)
                ),
                Triple(
                    R.string.base_body_category_task_dry_brushing,
                    1,
                    TaskSchedule(1, TaskFrequency.DAY)
                ),
                Triple(
                    R.string.base_body_category_task_spa,
                    1,
                    TaskSchedule(1, TaskFrequency.MONTH)
                ),
                Triple(
                    R.string.base_body_category_task_butter,
                    1,
                    TaskSchedule(1, TaskFrequency.WEEK)
                ),
                Triple(
                    R.string.base_category_task_other,
                    1,
                    TaskSchedule(2, TaskFrequency.WEEK)
                )
            )
            BaseCategoryEnum.FEET -> listOf(
                Triple(
                    R.string.base_feet_category_task_pedicure,
                    2,
                    TaskSchedule(3, TaskFrequency.WEEK)
                ),
                Triple(
                    R.string.base_feet_category_task_depilation,
                    2,
                    TaskSchedule(1, TaskFrequency.MONTH)
                ),
                Triple(
                    R.string.base_feet_category_task_mask,
                    1,
                    TaskSchedule(3, TaskFrequency.DAY)
                ),
                Triple(
                    R.string.base_feet_category_task_cream,
                    1,
                    TaskSchedule(2, TaskFrequency.DAY)
                ),
                Triple(
                    R.string.base_feet_category_task_anti_cellulite,
                    1,
                    TaskSchedule(3, TaskFrequency.DAY)
                ),
                Triple(
                    R.string.base_feet_category_task_massage,
                    1,
                    TaskSchedule(1, TaskFrequency.WEEK)
                ),
                Triple(
                    R.string.base_category_task_other,
                    1,
                    TaskSchedule(1, TaskFrequency.WEEK)
                )
            )
            BaseCategoryEnum.ORAL -> listOf(
                Triple(
                    R.string.base_oral_category_task_dentist,
                    3,
                    TaskSchedule(2, TaskFrequency.YEAR)
                ),
                Triple(
                    R.string.base_oral_category_task_oral_hygiene,
                    2,
                    TaskSchedule(2, TaskFrequency.YEAR)
                ),
                Triple(
                    R.string.base_oral_category_task_irrigator,
                    2,
                    TaskSchedule(3, TaskFrequency.DAY)
                ),
                Triple(
                    R.string.base_oral_category_task_whitening,
                    2,
                    TaskSchedule(3, TaskFrequency.MONTH)
                ),
                Triple(
                    R.string.base_oral_category_task_dental_floss,
                    1,
                    TaskSchedule(1, TaskFrequency.DAY)
                ),
                Triple(
                    R.string.base_category_task_other,
                    1,
                    TaskSchedule(1, TaskFrequency.MONTH)
                )
            )
            BaseCategoryEnum.OTHER -> listOf(
                Triple(
                    R.string.base_other_category_task_daily,
                    1,
                    TaskSchedule(1, TaskFrequency.DAY)
                ),
                Triple(
                    R.string.base_other_category_task_weekly,
                    1,
                    TaskSchedule(1, TaskFrequency.WEEK)
                ),
                Triple(
                    R.string.base_other_category_task_biweekly,
                    1,
                    TaskSchedule(2, TaskFrequency.WEEK)
                ),
                Triple(
                    R.string.base_other_category_task_monthly,
                    1,
                    TaskSchedule(1, TaskFrequency.MONTH)
                ),
                Triple(
                    R.string.base_other_category_task_annual,
                    1,
                    TaskSchedule(1, TaskFrequency.YEAR)
                )
            )
        }
    }

    enum class BaseCategoryEnum {
        FACE, HANDS, HAIR, BODY, FEET, ORAL, OTHER
    }

    private fun Context.resNameById(resId: Int): String {
        return resources.getResourceEntryName(resId)
    }
}
