package com.beautydiary.core_ui.bindings

import android.view.View
import android.widget.LinearLayout
import androidx.databinding.BindingAdapter


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
    this.visibility = if (value) View.GONE else View.VISIBLE
}

@BindingAdapter("goneIfNot")
fun View.goneIfNot(value: Boolean) {
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

@BindingAdapter("invisibleIf")
fun View.invisibleIf(value: Boolean) {
    this.visibility = if (value) View.INVISIBLE else View.VISIBLE
}

@BindingAdapter("invisibleIfNot")
fun View.invisibleIfNot(value: Boolean) {
    this.visibility = if (!value) View.INVISIBLE else View.VISIBLE
}

@BindingAdapter("android:layout_weight")
fun View.setLayoutWeight(weight: Int) {
    val params = layoutParams as? LinearLayout.LayoutParams
    params?.let {
        it.weight = weight.toFloat()
        layoutParams = it
    }
}