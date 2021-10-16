package com.amalkina.beautydiary.di

import android.app.Application
import androidx.room.Room
import com.amalkina.beautydiary.data.repositories.*
import com.amalkina.beautydiary.data.room.AppDatabase
import com.amalkina.beautydiary.data.room.dao.CategoryDao
import com.amalkina.beautydiary.domain.interfaces.*
import com.amalkina.beautydiary.domain.usecases.*
import com.amalkina.beautydiary.ui.home.vm.AddCategoryViewModel
import com.amalkina.beautydiary.ui.home.vm.HomeViewModel
import com.amalkina.beautydiary.ui.tasks.vm.AddTaskViewModel
import com.amalkina.beautydiary.ui.tasks.vm.TaskListViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module


val dataModule = module {
    single<CategoryRepository> { CategoryRepositoryImpl() }

    // db
    single { AppDatabase.buildDatabase(androidApplication()) }
    single { get<AppDatabase>().categoryDao() }
}

val domainModule = module {
    single { ReadWriteImageUseCase(androidApplication()) }
    single { GetCategoriesUseCase() }
    single { GetCategoryUseCase() }
    single { UpdateCategoryUseCase() }
}

val viewModelModule = module {
    viewModel { HomeViewModel() }
    viewModel { AddCategoryViewModel(it[0]) }
    viewModel { TaskListViewModel() }
    viewModel { AddTaskViewModel(it[0], it[1]) }
}