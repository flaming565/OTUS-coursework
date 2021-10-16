package com.amalkina.beautydiary.ui.home.models

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.DrawableRes
import com.amalkina.beautydiary.R
import com.amalkina.beautydiary.ui.common.ext.getDrawableRes
import com.amalkina.beautydiary.ui.common.models.BaseModel

// TODO: calculate progress
internal open class HomeCategory(
    val id: Long = 0,
    val name: String = "",
    val progress: Int = BASE_PROGRESS,
    val imagePath: String? = null,
    @DrawableRes val imageDrawable: Int? = null
) : BaseModel() {

    val bitmap: Bitmap? = imagePath?.let { BitmapFactory.decodeFile(it) }
    val drawable = imageDrawable?.let { getContext().getDrawableRes(it) }

    companion object {
        const val BASE_PROGRESS = 50
    }
}

internal object HomeCategoryNew :
    HomeCategory(name = getString(R.string.home_new_category), imageDrawable = R.drawable.home_category_add)