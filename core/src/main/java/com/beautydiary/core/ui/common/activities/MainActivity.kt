package com.beautydiary.core.ui.common.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.beautydiary.core.R
import com.beautydiary.navigation.NavigationFlow
import com.beautydiary.navigation.Navigator
import com.beautydiary.navigation.ToFlowNavigatable

class MainActivity : AppCompatActivity(), ToFlowNavigatable {

    private val navigator: Navigator = Navigator()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navController = findNavController(R.id.nav_host_fragment)
        navigator.navController = navController
    }

    override fun navigateToFlow(flow: NavigationFlow) {
        navigator.navigateToFlow(flow)
    }
}