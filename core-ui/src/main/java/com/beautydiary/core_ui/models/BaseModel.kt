package com.beautydiary.core_ui.models

import android.content.Context
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import com.beautydiary.core_ui.ext.getStringRes
import org.koin.core.context.GlobalContext.get

open class BaseModel {
    companion object {
        fun getString(@StringRes id: Int, vararg args: Any) = getContext().getStringRes(id, *args)
        fun getPlurals(@PluralsRes id: Int, quantity: Int, vararg args: Any) = getContext().resources.getQuantityString(id, quantity, *args)
        fun getContext(): Context = get().get()
    }
}