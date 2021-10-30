package com.amalkina.beautydiary.data.repositories

import com.amalkina.beautydiary.data.common.BaseRepository
import com.amalkina.beautydiary.data.common.toDomain
import com.amalkina.beautydiary.data.network.QuoteApiInterface
import com.amalkina.beautydiary.domain.interfaces.QuoteRepository
import com.amalkina.beautydiary.domain.models.DomainQuote
import org.koin.core.component.inject
import java.util.*

internal class QuoteRepositoryImpl : QuoteRepository, BaseRepository() {
    private val api by inject<QuoteApiInterface>()

    override suspend fun getRandomQuote(): DomainQuote {
        return api.getRandomQuote(getCurrentLanguage()).toDomain()
    }

    override suspend fun getQuote(key: Int): DomainQuote {
        return api.getQuote(key, getCurrentLanguage()).toDomain()
    }

    private fun getCurrentLanguage() = Locale.getDefault().language
}