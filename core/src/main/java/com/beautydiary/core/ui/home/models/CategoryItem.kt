package com.beautydiary.core.ui.home.models

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes
import com.beautydiary.core.R
import com.beautydiary.core.ui.common.ext.getDrawableRes
import com.beautydiary.core.ui.common.ext.resIdByName
import com.beautydiary.core.ui.common.models.BaseModel

sealed class CategoryItem(
    open val id: Long = -1,
    open val name: String = "",
    @DrawableRes open val imageDrawable: Int? = null
) : BaseModel()

data class HomeCategory(
    override val id: Long = -1,
    val baseCategoryId: Long = 0,
    override var name: String = "",
    val stringResName: String? = null,
    val imagePath: String? = null,
    val drawableName: String? = null,
    @DrawableRes override var imageDrawable: Int? = null,
    val progress: Int = 0
) : CategoryItem(id = id, name = name, imageDrawable = imageDrawable) {

    init {
        stringResName?.let { name = getString(getContext().resIdByName(it, "string")) }
        drawableName?.let { imageDrawable = getContext().resIdByName(it, "drawable") }
    }

    val bitmap: Bitmap? = imagePath?.let { BitmapFactory.decodeFile(it) }
    val drawable = imageDrawable?.let { getContext().getDrawableRes(it) }

    val hideProgressView = progress == 0
}

internal object HomeCategoryNew : CategoryItem(
    name = getString(R.string.home_new_category),
    imageDrawable = R.drawable.home_category_add
) {
    val drawable = imageDrawable?.let { getContext().getDrawableRes(it) }
}