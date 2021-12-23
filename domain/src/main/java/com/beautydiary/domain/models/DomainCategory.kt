package com.beautydiary.domain.models

import android.os.Parcelable
import com.beautydiary.domain.common.LightColor
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize


@Parcelize
data class DomainCategory(
    val id: Long = -1,
    val baseCategoryId: Long = -1,
    val name: String = "",
    val stringResName: String? = null,
    val imagePath: String? = null,
    val drawableName: String? = null,
    val updateDate: Long = System.currentTimeMillis(),
    val creationDate: Long = System.currentTimeMillis()
) : Parcelable {

    @IgnoredOnParcel
    val color = LightColor.generate()
}