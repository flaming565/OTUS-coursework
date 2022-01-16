package com.beautydiary.feature_tasks.di

import com.beautydiary.feature_tasks.vm.*
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val tasksModule = module {
    viewModel { TodoListViewModel() }
    viewModel { TaskListViewModel(it[0]) }
    viewModel { AddTaskViewModel(it[0], it[1]) }
    viewModel { TaskDetailViewModel(it[0]) }
}