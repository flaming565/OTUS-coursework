package com.amalkina.beautydiary.domain.interfaces

import com.amalkina.beautydiary.domain.models.DomainQuote

internal interface QuoteRepository {
    suspend fun getRandomQuote(): DomainQuote
    suspend fun getQuote(key: Int): DomainQuote
}