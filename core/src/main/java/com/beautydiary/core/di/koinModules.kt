package com.beautydiary.core.di

import com.beautydiary.core.data.network.QuoteApiInterface
import com.beautydiary.core.data.repositories.CategoryRepositoryImpl
import com.beautydiary.core.data.repositories.QuoteRepositoryImpl
import com.beautydiary.core.data.repositories.TaskRepositoryImpl
import com.beautydiary.core.data.room.AppDatabase
import com.beautydiary.core.domain.common.ApplicationSettings
import com.beautydiary.core.domain.interfaces.CategoryRepository
import com.beautydiary.core.domain.interfaces.QuoteRepository
import com.beautydiary.core.domain.interfaces.TaskRepository
import com.beautydiary.core.domain.usecases.CategoryActionsUseCase
import com.beautydiary.core.domain.usecases.GetQuoteUseCase
import com.beautydiary.core.domain.usecases.ReadWriteImageUseCase
import com.beautydiary.core.domain.usecases.TaskActionsUseCase
import com.beautydiary.core.ui.home.vm.AddCategoryViewModel
import com.beautydiary.core.ui.home.vm.HomeViewModel
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidApplication
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit


@ExperimentalSerializationApi
val dataModule = module {
    single<CategoryRepository> { CategoryRepositoryImpl() }
    single<TaskRepository> { TaskRepositoryImpl() }
    single<QuoteRepository> { QuoteRepositoryImpl() }

    // db
    single { AppDatabase.getInstance(androidApplication()) }
    single { get<AppDatabase>().baseCategoryDao() }
    single { get<AppDatabase>().baseTaskDao() }
    single { get<AppDatabase>().categoryDao() }
    single { get<AppDatabase>().taskDao() }

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
    single { get<Retrofit>().create(QuoteApiInterface::class.java) }
}

val domainModule = module {
    single { ReadWriteImageUseCase(androidApplication()) }
    single { GetQuoteUseCase() }
    single { CategoryActionsUseCase() }
    single { TaskActionsUseCase() }

    single { ApplicationSettings() }
}

val viewModelModule = module {
    viewModel { HomeViewModel() }
    viewModel { AddCategoryViewModel(it[0]) }
}