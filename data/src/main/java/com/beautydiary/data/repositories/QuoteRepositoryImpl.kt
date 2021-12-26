package com.beautydiary.data.repositories

import com.beautydiary.data.common.BaseRepository
import com.beautydiary.data.common.toDomain
import com.beautydiary.domain.interfaces.QuoteRepository
import com.beautydiary.domain.models.DomainQuote
import org.koin.core.component.inject
import java.util.*


internal class QuoteRepositoryImpl : QuoteRepository, BaseRepository() {
    private val api by inject<com.beautydiary.data.network.QuoteApiInterface>()

    override suspend fun getRandomQuote(): DomainQuote {
        return api.getRandomQuote(getCurrentLanguage()).toDomain()
    }

    override suspend fun getQuote(key: Int): DomainQuote {
        return api.getQuote(key, getCurrentLanguage()).toDomain()
    }

    private fun getCurrentLanguage() = Locale.getDefault().language
}