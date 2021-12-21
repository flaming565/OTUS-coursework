package com.amalkina.beautydiary.ui.home.models

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes
import com.amalkina.beautydiary.R
import com.amalkina.beautydiary.ui.common.ext.getDrawableRes
import com.amalkina.beautydiary.ui.common.ext.resIdByName
import com.amalkina.beautydiary.ui.common.models.BaseModel

internal open class HomeCategory(
    val id: Long = -1,
    val baseCategoryId: Long = 0,
    var name: String = "",
    val stringResName: String? = null,
    val imagePath: String? = null,
    val drawableName: String? = null,
    @DrawableRes var imageDrawable: Int? = null,
    val progress: Int = 0
) : BaseModel() {

    init {
        stringResName?.let { name = getString(getContext().resIdByName(it, "string")) }
        drawableName?.let { imageDrawable = getContext().resIdByName(it, "drawable") }
    }

    val bitmap: Bitmap? = imagePath?.let { BitmapFactory.decodeFile(it) }
    val drawable = imageDrawable?.let { getContext().getDrawableRes(it) }

    val hideProgressView = progress == 0
}

internal object HomeCategoryNew :
    HomeCategory(name = getString(R.string.home_new_category), imageDrawable = R.drawable.home_category_add)