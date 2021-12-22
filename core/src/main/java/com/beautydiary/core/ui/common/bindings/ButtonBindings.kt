package com.beautydiary.core.ui.common.bindings

import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.databinding.BindingAdapter
import com.google.android.material.button.MaterialButton


@BindingAdapter("iconEnabled")
fun MaterialButton.setIconEnabled(value: Boolean) {
    isEnabled = value
    if (value)
        icon.colorFilter = null
    else
        icon = convertDrawableToGrayScale(icon)
}

fun convertDrawableToGrayScale(drawable: Drawable) = drawable.also {
    it.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(
        Color.LTGRAY,
        BlendModeCompat.SRC_IN
    )
}