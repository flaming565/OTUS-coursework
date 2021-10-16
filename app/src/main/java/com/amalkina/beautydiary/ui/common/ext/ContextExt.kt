package com.amalkina.beautydiary.ui.common.ext

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat

fun Context.hasPermission(permission: String): Boolean {
    return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
}

fun Context.getStringRes(@StringRes id: Int, vararg args: Any): String {
    return resources.getString(id, *args)
}

fun Context.getDrawableRes(@DrawableRes id: Int): Drawable? {
    return ResourcesCompat.getDrawable(resources, id, theme)
}

fun Context.resIdByName(resIdName: String, resType: String): Int {
    return resources.getIdentifier(resIdName, resType, packageName)
}

fun Context.resNameById(resId: Int): String {
    return resources.getResourceEntryName(resId)
}