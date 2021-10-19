package com.amalkina.beautydiary.ui.common.ext

import android.content.Context
import com.amalkina.beautydiary.domain.models.Category
import com.amalkina.beautydiary.ui.home.models.HomeCategory
import org.koin.core.context.GlobalContext.get

internal fun Category.toHomeCategory() = HomeCategory(
    id = this.id,
    name = this.name,
    imagePath = this.imagePath,
    imageDrawable = this.drawableName?.let { getContext().resIdByName(it, "drawable") }
)

internal fun HomeCategory.toCategory() = Category(
    id = this.id,
    name = this.name,
    imagePath = this.imagePath,
    drawableName = this.imageDrawable?.let { getContext().resNameById(it) }
)

private fun getContext(): Context {
    return get().get()
}