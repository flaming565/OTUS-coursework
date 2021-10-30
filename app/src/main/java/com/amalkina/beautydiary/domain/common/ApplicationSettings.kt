package com.amalkina.beautydiary.domain.common

import android.content.Context
import com.amalkina.beautydiary.domain.ext.boolean
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

class ApplicationSettings : KoinComponent {
    private val prefs = get<Context>().getSharedPreferences("ApplicationSettings", Context.MODE_PRIVATE)

    var shouldQuoteBeShown by prefs.boolean(true)
}