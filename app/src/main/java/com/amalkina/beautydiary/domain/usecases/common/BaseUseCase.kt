package com.amalkina.beautydiary.domain.usecases.common

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import org.koin.core.component.KoinComponent
import kotlin.coroutines.CoroutineContext


abstract class BaseUseCase : KoinComponent, CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO
}
