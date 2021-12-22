package com.beautydiary.core.domain.usecases

import com.beautydiary.core.domain.interfaces.QuoteRepository
import org.koin.core.component.inject


internal class GetQuoteUseCase : BaseUseCase() {
    private val repository by inject<QuoteRepository>()
    // todo: generate once a day
    private val key = 12345

    suspend fun randomQuote() =
        suspendResult(Unit) { repository.getRandomQuote() }

    suspend fun dailyQuote() =
        suspendResult(Unit) { repository.getQuote(key) }
}