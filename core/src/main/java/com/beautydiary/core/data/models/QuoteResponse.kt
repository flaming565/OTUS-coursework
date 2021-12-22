package com.beautydiary.core.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
internal data class QuoteResponse(
    @SerialName("quoteText")
    var quoteText: String = "",
    @SerialName("quoteAuthor")
    var quoteAuthor: String = "",
    @SerialName("quoteLink")
    var quoteLink: String = "",
)
