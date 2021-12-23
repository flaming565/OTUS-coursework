package com.beautydiary.core.domain.interfaces

import com.beautydiary.domain.models.DomainQuote

internal interface QuoteRepository {
    suspend fun getRandomQuote(): DomainQuote
    suspend fun getQuote(key: Int): DomainQuote
}