package com.amalkina.beautydiary.ui.common.ext

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

fun <T, M> StateFlow<T>.map(
    coroutineScope: CoroutineScope,
    mapper: (value: T) -> M
): StateFlow<M> = map { mapper(it) }.stateIn(
    coroutineScope,
    SharingStarted.Lazily,
    mapper(value)
)

fun <T, M> StateFlow<T>.mapToMutable(
    coroutineScope: CoroutineScope,
    mapper: (value: T) -> M
): MutableStateFlow<M> {
    val mFlow = MutableStateFlow(mapper(value))
    coroutineScope.launch {
        collect {
            mFlow.emit(mapper(it))
        }
    }
    return mFlow
}