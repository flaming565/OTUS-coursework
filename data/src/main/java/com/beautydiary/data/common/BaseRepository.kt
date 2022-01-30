package com.beautydiary.data.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.core.component.KoinComponent
import kotlin.coroutines.CoroutineContext


internal abstract class BaseRepository : KoinComponent, CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO

}