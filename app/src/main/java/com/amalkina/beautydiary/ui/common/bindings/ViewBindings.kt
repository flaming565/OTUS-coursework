package com.amalkina.beautydiary.ui.common.bindings

import android.view.View
import android.widget.LinearLayout
import androidx.databinding.BindingAdapter
import timber.log.Timber

@BindingAdapter("goneIfNot")
fun View.goneIfNot(value: String?) {
    this.visibility = if (value.isNullOrEmpty()) View.GONE else View.VISIBLE
}

@BindingAdapter("goneIf")
fun View.goneIf(value: String?) {
    this.visibility = if (value.isNullOrEmpty()) View.VISIBLE else View.GONE
}

@BindingAdapter("goneIf")
fun View.goneIf(value: Boolean) {
    Timber.d("ddd test goneIf $value")
    this.visibility = if (value) View.GONE else View.VISIBLE
}

@BindingAdapter("goneIfNot")
fun View.goneIfNot(value: Boolean) {
    Timber.d("ddd test goneIfNot $value")
    this.visibility = if (!value) View.GONE else View.VISIBLE
}

@BindingAdapter("goneIfNot")
fun View.goneIfNot(value: Any?) {
    this.visibility = if (value == null) View.GONE else View.VISIBLE
}

@BindingAdapter("goneIf")
fun View.goneIf(value: Any?) {
    this.visibility = if (value == null) View.VISIBLE else View.GONE
}

@BindingAdapter("android:layout_weight")
fun View.setLayoutWeight(weight: Int) {
    val params = layoutParams as? LinearLayout.LayoutParams
    params?.let {
        it.weight = weight.toFloat()
        layoutParams = it
    }
}