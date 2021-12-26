package com.beautydiary.domain.interfaces

import com.beautydiary.domain.models.DomainQuote

interface QuoteRepository {
    suspend fun getRandomQuote(): DomainQuote
    suspend fun getQuote(key: Int): DomainQuote
}