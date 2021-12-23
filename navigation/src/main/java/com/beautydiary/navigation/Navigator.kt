package com.beautydiary.navigation

import androidx.navigation.NavController

class Navigator {
    lateinit var navController: NavController

    fun navigateToFlow(navigationFlow: NavigationFlow) = when (navigationFlow) {
        NavigationFlow.StatisticsFlow -> navController.navigate(NavGraphDirections.toStatisticsFlow())
        is NavigationFlow.TasksFlow -> navController.navigate(
            NavGraphDirections.toTasksFlow(
                navigationFlow.category
            )
        )
        NavigationFlow.TodoListFlow -> navController.navigate(NavGraphDirections.toTodoListFlow())
    }
}