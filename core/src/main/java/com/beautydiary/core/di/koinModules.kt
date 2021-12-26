package com.beautydiary.core.di

import com.beautydiary.core.domain.common.ApplicationSettings
import org.koin.dsl.module


val coreDomainModule = module {
    single { ApplicationSettings() }
}