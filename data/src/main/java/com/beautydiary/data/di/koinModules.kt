package com.beautydiary.data.di

import com.beautydiary.domain.interfaces.CategoryRepository
import com.beautydiary.domain.interfaces.QuoteRepository
import com.beautydiary.domain.interfaces.TaskRepository
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit


@ExperimentalSerializationApi
val dataModule = module {
    single<CategoryRepository> { com.beautydiary.data.repositories.CategoryRepositoryImpl() }
    single<TaskRepository> { com.beautydiary.data.repositories.TaskRepositoryImpl() }
    single<QuoteRepository> { com.beautydiary.data.repositories.QuoteRepositoryImpl() }

    // db
    single { com.beautydiary.data.room.AppDatabase.getInstance(androidApplication()) }
    single { get<com.beautydiary.data.room.AppDatabase>().baseCategoryDao() }
    single { get<com.beautydiary.data.room.AppDatabase>().baseTaskDao() }
    single { get<com.beautydiary.data.room.AppDatabase>().categoryDao() }
    single { get<com.beautydiary.data.room.AppDatabase>().taskDao() }

    // retrofit

    single {
        OkHttpClient.Builder().apply {
            connectTimeout(5, TimeUnit.SECONDS)
            readTimeout(60, TimeUnit.SECONDS)
            writeTimeout(60, TimeUnit.SECONDS)
        }.build()
    }
    single<Retrofit> {
        val json = Json { ignoreUnknownKeys = true }
        val contentType = "application/json".toMediaType()
        Retrofit.Builder()
            .client(get())
            .baseUrl("https://api.forismatic.com/")
            .addConverterFactory(json.asConverterFactory(contentType))
            .build()
    }
    single { get<Retrofit>().create(com.beautydiary.data.network.QuoteApiInterface::class.java) }
}