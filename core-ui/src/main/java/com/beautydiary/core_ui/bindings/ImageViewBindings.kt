package com.beautydiary.core_ui.bindings

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter

@BindingAdapter(value = ["imageBitmap", "imageDrawable"], requireAll = false)
fun ImageView.loadImage(bitmap: Bitmap?, drawable: Drawable?) {
    if (bitmap != null)
        setImageBitmap(bitmap)
    else if (drawable != null)
        setImageDrawable(drawable)
}