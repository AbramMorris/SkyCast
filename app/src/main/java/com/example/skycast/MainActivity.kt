package com.example.skycast


import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.skycast.Remote.WeatherApiServes
import com.example.skycast.Remote.WeatherRemoteDataSourceImpl
import com.example.skycast.Reposatory.WeatherRepositoryImpl
import com.example.skycast.UiI.Nav.AppNavGraph
import com.example.skycast.UiI.Nav.navBar
import com.example.skycast.Utlis.LocationHelper
import com.example.skycast.Utlis.REQUEST_LOCATION_PERMISSION
import com.example.skycast.ViewModel.WeatherViewModel
import com.example.skycast.ViewModel.WeatherViewModelFactory


class MainActivity : ComponentActivity() {
    lateinit var viewModel: WeatherViewModel
    private lateinit var locationHelper : LocationHelper
    private lateinit var locationState : MutableState<Location?>

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationHelper = LocationHelper(this)
        locationState = mutableStateOf<Location?>(null)
        enableEdgeToEdge()
        setContent {

            MainNavigation()
            val apiService = WeatherApiServes.create()
            val remoteDataSource = WeatherRemoteDataSourceImpl(apiService)
            val repository = WeatherRepositoryImpl(remoteDataSource)
            val viewModelFactory = WeatherViewModelFactory(repository)
            viewModel = ViewModelProvider(this, viewModelFactory)[WeatherViewModel::class.java]
        }
        if (!locationHelper.hasLocationPermissions()) {
            locationHelper.requestLocationPermissions(this)
        } else {
            fetchLocation()
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLocation()
            } else {
                Log.e("TAG", "MainActivity Permission denied by user.")
            }
        }
    }

    override fun onStart() {
        super.onStart()

        if (locationHelper.hasLocationPermissions()) {
            if (locationHelper.isLocationEnabled()) {
                fetchLocation()
            } else {
                locationHelper.enableLocation()
            }
        } else {
            locationHelper.requestLocationPermissions(this)
        }
    }
    private fun fetchLocation() {
        locationHelper.getFreshLocation { location ->
            locationState.value = location
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    @Composable
    fun MainNavigation() {

        val navController = rememberNavController()

        Scaffold(
            modifier = Modifier
                .fillMaxSize(),
            contentWindowInsets = ScaffoldDefaults.contentWindowInsets
            ,
            bottomBar = {
                val currentBackStackEntry = navController.currentBackStackEntryAsState()
                val currentRoute = currentBackStackEntry.value?.destination?.route
                if (currentRoute != ScreenRoute.Splash.route){
                navBar(navController)}
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                AppNavGraph(navController = navController, viewModel = viewModel)
            }
        }
    }

}




