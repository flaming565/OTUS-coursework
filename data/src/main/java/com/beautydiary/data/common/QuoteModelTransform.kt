package com.beautydiary.data.common

import com.beautydiary.data.models.QuoteResponse
import com.beautydiary.domain.models.DomainQuote

internal fun QuoteResponse.toDomain() = DomainQuote(
    quote = quoteText,
    author = quoteAuthor
)