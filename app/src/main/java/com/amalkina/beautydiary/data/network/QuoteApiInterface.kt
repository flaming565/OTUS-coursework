package com.amalkina.beautydiary.data.network

import com.amalkina.beautydiary.data.models.QuoteResponse
import retrofit2.http.GET
import retrofit2.http.Query

internal interface QuoteApiInterface {
    @GET("api/1.0/")
    suspend fun getRandomQuote(
        @Query("lang") lang: String,
        @Query("method") method: String = "getQuote",
        @Query("format") format: String = "json"
    ): QuoteResponse

    @GET("api/1.0/")
    suspend fun getQuote(
        @Query("key") key: Int,
        @Query("lang") lang: String,
        @Query("method") method: String = "getQuote",
        @Query("format") format: String = "json"
    ): QuoteResponse
}