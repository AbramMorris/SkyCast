package com.example.skycast


import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.skycast.data.database.AlarmDataBase.AlarmLocalDataSource
import com.example.skycast.data.database.FavDataBase.AppDatabase
import com.example.skycast.data.database.FavDataBase.LocalDataSource
import com.example.skycast.data.remotes.WeatherApiServes
import com.example.skycast.data.remotes.WeatherRemoteDataSourceImpl
import com.example.skycast.data.repo.WeatherRepositoryImpl
import com.example.skycast.ui.navigation.AppNavGraph
import com.example.skycast.ui.navigation.ScreenRoute
import com.example.skycast.ui.navigation.navBar
import com.example.skycast.util.LocationHelper
import com.example.skycast.util.NetworkHelper
import com.example.skycast.util.REQUEST_LOCATION_PERMISSION
import com.example.skycast.util.loadLanguagePreference
import com.example.skycast.viewmodel.AlarmViewModel
import com.example.skycast.viewmodel.AlertViewModelFactory
import com.example.skycast.viewmodel.WeatherViewModel
import com.example.skycast.viewmodel.WeatherViewModelFactory
import java.util.Locale


class MainActivity : ComponentActivity() {
    lateinit var viewModel: WeatherViewModel
    lateinit var alarmViewModel: AlarmViewModel
    private lateinit var locationHelper : LocationHelper
    private lateinit var locationState : MutableState<Location?>

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationHelper = LocationHelper(this)
        locationState = mutableStateOf<Location?>(null)
        enableEdgeToEdge()
        val lng = intent.getDoubleExtra("longitude", -1.0)
        val lat = intent.getDoubleExtra("latitude", -1.0)
        Log.d("TAG", "onCreate: $lat $lng")


        applyLanguage(loadLanguagePreference(this))
        Log.d("loadLanguagePreference", "onCreate: ${loadLanguagePreference(this)}")
        setContent {
            MainNavigation(lat,lng)
            val apiService = WeatherApiServes.create()
            val remoteDataSource = WeatherRemoteDataSourceImpl(apiService)
            val local = LocalDataSource(AppDatabase.getDatabase(this).locationDao())
            val repository = WeatherRepositoryImpl(remoteDataSource,local)
            val alertLocalDataSource = AlarmLocalDataSource(AppDatabase.getDatabase(this).alarmDao())
            val alarmRepository = com.example.skycast.data.repo.AlarmRepoImp(alertLocalDataSource)
            val homeLocaleDataSource = com.example.skycast.data.database.HomeDataBase.HomeLocalDataSource(AppDatabase.getDatabase(this).homeDao())
            val repositoryHome = com.example.skycast.data.repo.HomeCacheRepo( homeLocaleDataSource)
            val viewModelFactory = WeatherViewModelFactory(repository,repositoryHome)
            val alarmViewModelFactory = AlertViewModelFactory(alarmRepository)
            viewModel = ViewModelProvider(this, viewModelFactory)[WeatherViewModel::class.java]
            alarmViewModel = ViewModelProvider(this,alarmViewModelFactory)[AlarmViewModel::class.java]
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
    fun MainNavigation(notificationLatitude: Double,
                       notificationLongitude: Double) {
        val navController = rememberNavController()
        val context = LocalContext.current
//        val isNetworkAvailable = remember { mutableStateOf(true) }


//        LaunchedEffect(Unit) {
//            isNetworkAvailable.value = NetworkHelper.isNetworkAvailable(context)
//        }
        Scaffold(
            modifier = Modifier
                .fillMaxSize(),
            contentWindowInsets = ScaffoldDefaults.contentWindowInsets
            ,
            bottomBar = {
                val currentBackStackEntry = navController.currentBackStackEntryAsState()
                val currentRoute = currentBackStackEntry.value?.destination?.route
                if (currentRoute != ScreenRoute.Splash.route){
                navBar(navController)
                }
            },
            topBar = {
//                if (!isNetworkAvailable.value) {
//                    Box(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .background(Color.Red),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Text(
//                            text = stringResource(R.string.no_internet_connection),
//                            color = Color.White,
//                            modifier = Modifier.padding(8.dp)
//                        )
//                    }
//                }
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                AppNavGraph(navController = navController, viewModel = viewModel , alarmViewModel = alarmViewModel,notificationLatitude,notificationLongitude)
            }
        }
    }

    private fun applyLanguage(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }

}