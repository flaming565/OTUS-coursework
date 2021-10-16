package com.amalkina.beautydiary.ui.home.models

import com.amalkina.beautydiary.R

internal sealed class BaseCategory(name: String, image: Int) : HomeCategory(name = name, imageDrawable = image) {
    object FaceCategory :
        BaseCategory(getString(R.string.add_category_category_face), R.drawable.common_category_face)

    object HandsCategory :
        BaseCategory(getString(R.string.add_category_category_hands), R.drawable.common_category_hands)

    object HairCategory :
        BaseCategory(getString(R.string.add_category_category_hair), R.drawable.common_category_hair)

    object BodyCategory :
        BaseCategory(getString(R.string.add_category_category_body), R.drawable.common_category_body)

    object LegsCategory :
        BaseCategory(getString(R.string.add_category_category_legs), R.drawable.common_category_legs)

    object MouthCategory :
        BaseCategory(getString(R.string.add_category_category_mouth), R.drawable.common_category_mouth)

    object OtherCategory :
        BaseCategory(getString(R.string.add_category_category_other), R.drawable.common_category_other)
}

internal fun getBaseCategories(): List<BaseCategory> {
    return BaseCategory::class.sealedSubclasses.mapNotNull { it.objectInstance }
}