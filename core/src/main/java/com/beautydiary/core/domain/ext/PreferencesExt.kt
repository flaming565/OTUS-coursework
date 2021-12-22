package com.beautydiary.core.domain.ext

import android.content.SharedPreferences
import androidx.core.content.edit
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun SharedPreferences.boolean(default: Boolean = false, key: String? = null) =
    object : ReadWriteProperty<Any, Boolean> {

        override fun getValue(thisRef: Any, property: KProperty<*>): Boolean {
            return getBoolean(key ?: property.name, default)
        }

        override fun setValue(thisRef: Any, property: KProperty<*>, value: Boolean) = edit {
            putBoolean(key ?: property.name, value)
        }
    }
