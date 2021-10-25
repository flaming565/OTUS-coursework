package com.amalkina.beautydiary.domain.usecases

import com.amalkina.beautydiary.domain.common.Result
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import org.koin.core.component.KoinComponent
import kotlin.coroutines.CoroutineContext


abstract class BaseUseCase : KoinComponent, CoroutineScope {
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.IO

    internal fun <T, R> flowResult(arg: T, request: (T) -> R) = flow {
        emit(Result.Loading)
        try {
            emit(Result.Success(request.invoke(arg)))
        } catch (ex: Exception) {
            emit(Result.Error(ex.localizedMessage ?: ex.message ?: ""))
        }
    }

    internal suspend fun <T, R> suspendResult(arg: T, request: suspend (T) -> R) =
        try {
            Result.Success(request.invoke(arg))
        } catch (ex: Exception) {
            Result.Error(ex.localizedMessage ?: ex.message ?: "")
        }
}
