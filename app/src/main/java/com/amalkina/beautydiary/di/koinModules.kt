package com.amalkina.beautydiary.di

import com.amalkina.beautydiary.data.repositories.CategoryRepositoryImpl
import com.amalkina.beautydiary.data.repositories.TaskRepositoryImpl
import com.amalkina.beautydiary.data.room.AppDatabase
import com.amalkina.beautydiary.domain.interfaces.CategoryRepository
import com.amalkina.beautydiary.domain.interfaces.TaskRepository
import com.amalkina.beautydiary.domain.usecases.CategoryActionsUseCase
import com.amalkina.beautydiary.domain.usecases.ReadWriteImageUseCase
import com.amalkina.beautydiary.domain.usecases.TaskActionsUseCase
import com.amalkina.beautydiary.ui.home.vm.AddCategoryViewModel
import com.amalkina.beautydiary.ui.home.vm.HomeViewModel
import com.amalkina.beautydiary.ui.tasks.vm.AddTaskViewModel
import com.amalkina.beautydiary.ui.tasks.vm.TaskListViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module


val dataModule = module {
    single<CategoryRepository> { CategoryRepositoryImpl() }
    single<TaskRepository> { TaskRepositoryImpl() }

    // db
    single { AppDatabase.getInstance(androidApplication()) }
    single { get<AppDatabase>().baseCategoryDao() }
    single { get<AppDatabase>().baseTaskDao() }
    single { get<AppDatabase>().categoryDao() }
    single { get<AppDatabase>().taskDao() }
}

val domainModule = module {
    single { ReadWriteImageUseCase(androidApplication()) }

    single { CategoryActionsUseCase() }
    single { TaskActionsUseCase() }
}

val viewModelModule = module {
    viewModel { HomeViewModel() }
    viewModel { AddCategoryViewModel(it[0]) }
    viewModel { TaskListViewModel(it[0]) }
    viewModel { AddTaskViewModel(it[0], it[1]) }
}