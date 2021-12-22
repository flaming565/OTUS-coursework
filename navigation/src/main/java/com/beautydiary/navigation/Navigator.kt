package com.beautydiary.navigation

import androidx.navigation.NavController

class Navigator {
    lateinit var navController: NavController

    fun navigateToFlow(navigationFlow: NavigationFlow) = when (navigationFlow) {
        NavigationFlow.StatisticsFlow -> navController.navigate(NavGraphDirections.toStatisticsFlow())
    }
}