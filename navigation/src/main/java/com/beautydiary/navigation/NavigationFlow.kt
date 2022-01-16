package com.beautydiary.navigation

import com.beautydiary.domain.models.DomainCategory

sealed class NavigationFlow {
    object StatisticsFlow : NavigationFlow()
    class TasksFlow(val category: DomainCategory) : NavigationFlow()
    object TodoListFlow : NavigationFlow()
}