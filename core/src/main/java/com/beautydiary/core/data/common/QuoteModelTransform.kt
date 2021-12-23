package com.beautydiary.core.data.common

import com.beautydiary.domain.models.DomainQuote
import com.beautydiary.core.data.models.QuoteResponse

internal fun QuoteResponse.toDomain() = DomainQuote(
    quote = quoteText,
    author = quoteAuthor
)