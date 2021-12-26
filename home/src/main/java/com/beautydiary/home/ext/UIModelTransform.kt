package com.beautydiary.home.ext

import com.beautydiary.core_ui.ext.resNameById
import com.beautydiary.core_ui.models.BaseModel.Companion.getContext
import com.beautydiary.domain.models.DomainCategory
import com.beautydiary.domain.models.DomainCategoryWithTasks
import com.beautydiary.domain.models.DomainQuote
import com.beautydiary.home.models.HomeCategory
import com.beautydiary.home.models.QuoteModel


fun HomeCategory.toDomain() = DomainCategory(
    id = id,
    baseCategoryId = baseCategoryId,
    name = name,
    imagePath = imagePath,
    drawableName = imageDrawable?.let { getContext().resNameById(it) }
)

fun DomainCategory.toUIModel() = HomeCategory(
    id = this.id,
    baseCategoryId = this.baseCategoryId,
    name = this.name,
    stringResName = this.stringResName,
    imagePath = this.imagePath,
    drawableName = this.drawableName
)

fun DomainCategoryWithTasks.toUIModel() = HomeCategory(
    id = this.category.id,
    baseCategoryId = this.category.baseCategoryId,
    name = this.category.name,
    stringResName = this.category.stringResName,
    imagePath = this.category.imagePath,
    drawableName = this.category.drawableName,
    progress = this.progress
)

fun DomainQuote.toUIModel() = QuoteModel(
    quote = this.quote,
    author = this.author
)