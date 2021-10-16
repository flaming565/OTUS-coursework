package com.amalkina.beautydiary.ui.common.models

import android.content.Context
import androidx.annotation.StringRes
import com.amalkina.beautydiary.ui.common.ext.getStringRes
import org.koin.core.context.GlobalContext.get

internal open class BaseModel {
    companion object {
        fun getString(@StringRes id: Int, vararg args: Any) = getContext().getStringRes(id, args)
        fun getContext(): Context = get().get()
    }
}