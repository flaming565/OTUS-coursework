package com.beautydiary.home.di

import com.beautydiary.home.vm.AddCategoryViewModel
import com.beautydiary.home.vm.HomeViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val homeModule = module {
    viewModel { HomeViewModel() }
    viewModel { AddCategoryViewModel(it[0]) }
}