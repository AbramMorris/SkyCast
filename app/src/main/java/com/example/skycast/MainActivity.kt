package com.example.skycast

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.skycast.Remote.WeatherApiServes
import com.example.skycast.UiI.Nav.AppNavGraph
import com.example.skycast.UiI.Nav.navBar
import com.example.skycast.ViewModel.WeatherViewModel
import com.example.skycast.ViewModel.WeatherViewModelFactory


class MainActivity : ComponentActivity() {
    lateinit var viewModel: WeatherViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MainNavigation()
            val navController = rememberNavController()
             viewModel = ViewModelProvider(this, WeatherViewModelFactory(WeatherApiServes.create())).get(WeatherViewModel::class.java)
//            AppNavGraph(navController, viewModel)
        }
    }

    @Composable
    fun MainNavigation() {
        val navController = rememberNavController()

        Scaffold(
            modifier = Modifier
                .fillMaxSize(),
            contentWindowInsets = ScaffoldDefaults.contentWindowInsets
            ,
            bottomBar = {
                navBar(navController)
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                AppNavGraph(navController = navController, viewModel = viewModel)
            }
        }
    }
}





