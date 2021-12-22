package com.beautydiary.core.data.repositories

import com.beautydiary.core.data.common.BaseRepository
import com.beautydiary.core.data.common.toDomain
import com.beautydiary.core.data.network.QuoteApiInterface
import com.beautydiary.core.domain.interfaces.QuoteRepository
import com.beautydiary.core.domain.models.DomainQuote
import org.koin.core.component.inject
import java.util.*

internal class QuoteRepositoryImpl : QuoteRepository, com.beautydiary.core.data.common.BaseRepository() {
    private val api by inject<QuoteApiInterface>()

    override suspend fun getRandomQuote(): DomainQuote {
        return api.getRandomQuote(getCurrentLanguage()).toDomain()
    }

    override suspend fun getQuote(key: Int): DomainQuote {
        return api.getQuote(key, getCurrentLanguage()).toDomain()
    }

    private fun getCurrentLanguage() = Locale.getDefault().language
}