package com.amalkina.beautydiary.data.common

import com.amalkina.beautydiary.domain.models.DomainQuote
import com.amalkina.beautydiary.data.models.QuoteResponse

internal fun QuoteResponse.toDomain() = DomainQuote(
    quote = quoteText,
    author = quoteAuthor
)