package com.beautydiary.featurestatistics.di

import com.beautydiary.featurestatistics.vm.StatisticsViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val statisticsModule = module {
    viewModel { StatisticsViewModel() }
}