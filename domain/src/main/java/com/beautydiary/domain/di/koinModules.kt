package com.beautydiary.domain.di

import com.beautydiary.domain.usecases.CategoryActionsUseCase
import com.beautydiary.domain.usecases.GetQuoteUseCase
import com.beautydiary.domain.usecases.ReadWriteImageUseCase
import com.beautydiary.domain.usecases.TaskActionsUseCase
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val domainModule = module {
    single { ReadWriteImageUseCase(androidApplication()) }
    single { GetQuoteUseCase() }
    single { CategoryActionsUseCase() }
    single { TaskActionsUseCase() }
}