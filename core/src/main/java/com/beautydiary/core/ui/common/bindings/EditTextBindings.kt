package com.beautydiary.core.ui.common.bindings

import androidx.core.widget.addTextChangedListener
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.google.android.material.textfield.TextInputEditText

@BindingAdapter("isFocused")
fun TextInputEditText.bindFocused(value: Boolean) {
    if (value) {
        requestFocus()
        text?.let { setSelection(it.length) }
    } else if (hasFocus()) {
        clearFocus()
    }
}

@BindingAdapter(value = ["textWithSelection", "textWithSelectionAttrChanged"], requireAll = false)
fun TextInputEditText.setStringWithSelection(str: String?, listener: InverseBindingListener) {
    if (text.toString() != str) {
        setText(str)
        text?.let { setSelection(it.length) }
    }

    addTextChangedListener {
        if (it.toString() != str)
            listener.onChange()
    }
}

@InverseBindingAdapter(attribute = "textWithSelection", event = "textWithSelectionAttrChanged")
fun TextInputEditText.getStringWithSelection(): String {
    return text.toString()
}
