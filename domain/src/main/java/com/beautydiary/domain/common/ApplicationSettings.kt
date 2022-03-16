package com.beautydiary.domain.common

import android.content.Context
import com.beautydiary.domain.ext.boolean
import org.koin.core.component.KoinComponent
import org.koin.core.component.get


class ApplicationSettings : KoinComponent {
    private val prefs =
        get<Context>().getSharedPreferences("ApplicationSettings", Context.MODE_PRIVATE)

    var shouldQuoteBeShown by prefs.boolean(true)
        private set

    var shouldDeleteCategoryDialogBeShown by prefs.boolean(true)
        private set
    var shouldDeleteTaskDialogBeShown by prefs.boolean(true)
        private set

    fun switchModeDeleteCategoryDialog(value: Boolean) {
        shouldDeleteCategoryDialogBeShown = value
    }

    fun switchModeDeleteTaskDialog(value: Boolean) {
        shouldDeleteTaskDialogBeShown = value
    }
}