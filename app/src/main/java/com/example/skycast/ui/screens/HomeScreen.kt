package com.example.skycast.ui.screens

import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.skycast.data.models.WeatherDetailItem
import com.example.skycast.data.models.WeatherForecastResponse
import com.example.skycast.data.models.WeatherResponse
import com.example.skycast.R
import com.example.skycast.data.database.FavDataBase.AppDatabase
import com.example.skycast.data.database.FavDataBase.LocalDataSource
import com.example.skycast.data.database.HomeDataBase.HomeLocalDataSource
import com.example.skycast.data.models.Response
import com.example.skycast.data.remotes.WeatherApiServes
import com.example.skycast.data.remotes.WeatherRemoteDataSourceImpl
import com.example.skycast.data.repo.HomeCacheRepo
import com.example.skycast.data.repo.WeatherRepositoryImpl
import com.example.skycast.util.LocationHelper
import com.example.skycast.viewmodel.WeatherViewModel
import com.example.skycast.viewmodel.WeatherViewModelFactory
import com.example.skycast.viewmodel.getDayNameFromDate
import com.example.skycast.ui.theme.BlueBlackBack
import com.example.skycast.util.DrawableUtils.getWeatherIcon
import com.example.skycast.util.NetworkHelper
import com.example.skycast.util.formatNumberBasedOnLanguage
import com.example.skycast.util.formatTemperatureUnitBasedOnLanguage
import com.example.skycast.util.getLocationMethod
import com.example.skycast.util.getTemperatureUnit
import com.example.skycast.util.getWindSpeedUnit
import com.example.skycast.util.loadLanguagePreference
import com.example.skycast.util.shouldShowNetworkToast

//
//@RequiresApi(Build.VERSION_CODES.O)
//@Composable
//fun HomeForecastScreen(navController: NavController, viewModel: WeatherViewModel) {
//    val apiService = WeatherApiServes.create()
//    val remoteDataSource = WeatherRemoteDataSourceImpl(apiService)
//    val local = LocalDataSource(AppDatabase.getDatabase(LocalContext.current).locationDao())
//    val repository = WeatherRepositoryImpl(remoteDataSource, local)
//    val homeLocaleDataSource = HomeLocalDataSource(AppDatabase.getDatabase(LocalContext.current).homeDao())
//    val repositoryHome = HomeCacheRepo( homeLocaleDataSource)
//    val viewModel: WeatherViewModel = viewModel(factory = WeatherViewModelFactory(repository,repositoryHome))
//    val weatherState by viewModel.weatherState.collectAsState()
//    val forecastState by viewModel.forecastState.collectAsState()
//    val isLoading by viewModel.loading.collectAsState()
//    val errorMessage by viewModel.errorMessage.collectAsState()
//    val context = LocalContext.current
//    val locationHelper = LocationHelper(context)
//    val windUnit = getWindSpeedUnit(context)
//    val locationMethod by viewModel.locationMethod.collectAsStateWithLifecycle()
//    val locationSetting =getLocationMethod(context)
//
//    LaunchedEffect(locationSetting) {
//        if (locationSetting == "GPS") {
//            locationHelper.getFreshLocation { location ->
//                val lang = loadLanguagePreference(context)
//                val tempUnit = getTemperatureUnit(context, "Temp")
//                if (location != null) {
//                    viewModel.fetchWeather(location.latitude, location.longitude, lang, tempUnit, context)
//                    viewModel.fetchWeatherForecast(location.latitude, location.longitude, lang, tempUnit, context)
//                } else {
//                    viewModel.fetchWeather(-0.13, 51.51, "en", "metric")
//                    viewModel.fetchWeatherForecast(51.51, -0.13, "en", "metric")
//                }
//            }
//        } else {
//            val tempUnit = getTemperatureUnit(context, "Temp")
//            val lang = loadLanguagePreference(context)
//            viewModel.fetchWeather(viewModel.getSavedHomeLocation(context).longitude, viewModel.getSavedHomeLocation(context).latitude, lang, tempUnit, context)
//            viewModel.fetchWeatherForecast( viewModel.getSavedHomeLocation(context).longitude,viewModel.getSavedHomeLocation(context).latitude, lang, tempUnit, context)
//        }
//    }
//
//    if (NetworkHelper.isNetworkAvailable(context)) {
//        if (shouldShowNetworkToast(context)) {
//            Toast.makeText(context, "Network Available", Toast.LENGTH_SHORT).show()
//        }
//    } else {
//            Toast.makeText(
//                context,
//                stringResource(R.string.this_is_the_last_data_when_you_are_connected_to_the_internet),
//                Toast.LENGTH_SHORT
//            ).show()
//
//    }
//
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(
//                brush = Brush.verticalGradient(
//                    colors = listOf(
//                        Color(android.graphics.Color.parseColor("#022a9a")),
//                        Color(android.graphics.Color.parseColor("#5381ff"))
//                    )
//                )
//            )
//    ) {
//        if (isLoading) {
//            CircularProgressIndicator(
//                modifier = Modifier.align(Alignment.Center),
//                color = Color.White
//            )
//        } else if (errorMessage != null) {
//            Text(
//                text = errorMessage ?: stringResource(R.string.unknown_error),
//                color = Color.Red,
//                modifier = Modifier.align(Alignment.Center)
//            )
//        } else {
//            weatherState?.let { weather ->
//                Column(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .verticalScroll(rememberScrollState()),
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    // City Name
//                    Text(
//                        text = weather.name ?: "Unknown",
//                        fontSize = 30.sp,
//                        color = Color.White,
//                        modifier = Modifier.padding(top = 48.dp)
//                    )
//
//                    // Weather Image
//                    Image(
//                        painter = painterResource(id = getWeatherIcon(weather.weather.firstOrNull()?.main)),
//                        contentDescription = stringResource(R.string.weather_icon),
//                        modifier = Modifier
//                            .size(150.dp)
//                            .padding(top = 8.dp)
//                    )
//
//                    // Weather Description
//                    Text(
//                        text = weather.weather.firstOrNull()?.description ?: stringResource(R.string.no_description),
//                        fontSize = 19.sp,
//                        color = Color.White,
//                        modifier = Modifier.padding(top = 8.dp)
//                    )
//
//                    // Temperature
//                    val formattedTemp = formatNumberBasedOnLanguage(weather.main.temp.toInt().toString())
//                    val formattedUnit = formatTemperatureUnitBasedOnLanguage(getTemperatureUnit(context, "Temp") ?: "C", getTemperatureUnit(context, "Lang") ?: "en"
//                    )
//                    Log.d("for", "HomeForecastScreen: ${formattedTemp}")
//                    Text(
//                        text = "$formattedTemp $formattedUnit",
//                        fontSize = 65.sp,
//                        fontWeight = FontWeight.Bold,
//                        color = Color.White,
//                        modifier = Modifier.padding(top = 8.dp)
//                    )
//
//                    // Weather Details Row
//                    WeatherDetailsRow(weather, getTemperatureUnit(context, "Lang") ?: "en", windUnit)
//
//                    // Today's Forecast
//                    Text(
//                        text = stringResource(R.string.today),
//                        fontSize = 20.sp,
//                        color = Color.White,
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(horizontal = 24.dp, vertical = 8.dp)
//                    )
//
//                    LazyRow(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(horizontal = 20.dp),
//                        horizontalArrangement = Arrangement.spacedBy(16.dp)
//                    ) {
//                        forecastState?.list?.let { hourlyForecast ->
//                            items(hourlyForecast.take(6)) { item ->
//                                FutureModelViewHolder(item)
//                            }
//                        }
//                    }
//
//                    // Weekly Forecast
//                    Text(
//                        text = stringResource(R.string.weekly_forecast),
//                        fontSize = 20.sp,
//                        color = Color.White,
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(horizontal = 24.dp, vertical = 8.dp)
//                    )
//
//                    WeeklyForecast(forecastState)
//                }
//            }
//        }
//    }
//}
//




@Composable
fun FutureModelViewHolder(forecast: WeatherForecastResponse.ForecastItem) {
    val context = LocalContext.current
    val formattedTemp = formatNumberBasedOnLanguage(forecast.main.temp.toInt().toString())
    val formattedUnit = formatTemperatureUnitBasedOnLanguage((getTemperatureUnit(context, "Temp") ?: "C"), getTemperatureUnit(context, "Lang") ?: "en")

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            painter = painterResource(id = getWeatherIcon(forecast.weather.firstOrNull()?.main)),
            contentDescription = stringResource(R.string.weather_icon),
            modifier = Modifier.size(50.dp)
        )
        Text(
            text = "$formattedTemp $formattedUnit",
            color = Color.White,
            fontSize = 16.sp
        )
        Text(
            text = formatNumberBasedOnLanguage(forecast.dt_txt.substring(11, 16)), // Extracts time
            color = Color.LightGray,
            fontSize = 14.sp
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeeklyForecast(forecastState: WeatherForecastResponse?) {
    val context = LocalContext.current
    forecastState?.let { forecast ->
        val dailyAverages = forecast.list
            .groupBy { it.dt_txt.substring(0, 10) }
            .mapValues { (_, dailyData) ->
                val avgTemp = dailyData.map { it.main.temp }.average().toInt()
                val weatherIcon = dailyData.firstOrNull()?.weather?.firstOrNull()?.icon
                avgTemp to weatherIcon
            }
            .toList()
            .take(5)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            dailyAverages.forEachIndexed { _, (date, data) ->
                val (avgTemp, iconRes) = data
                val formattedTemp = formatNumberBasedOnLanguage(avgTemp.toString())
                val formattedUnit = formatTemperatureUnitBasedOnLanguage(getTemperatureUnit(context, "Temp") ?: "C", getTemperatureUnit(context, "Lang") ?: "en")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .padding(vertical = 10.dp)
                        .background(BlueBlackBack, shape = RoundedCornerShape(10.dp))
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text =  getDayNameFromDate(date),
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "$formattedTemp $formattedUnit",
                        color = Color.White,
                        fontSize = 20.sp
                    )
                    Image(
                        painter = painterResource(id = getWeatherIcon(iconRes)),
                        contentDescription = stringResource(R.string.weather_icon)
                    )
                }
            }
        }
    }
}

@Composable
fun WeatherDetailsRow(weather: WeatherResponse, selectedLanguage: String, windUnit: String) {
    Box(
        modifier = Modifier
            .width(700.dp)
            .height(150.dp)
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .background(
                BlueBlackBack,
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .width(700.dp)
                .height(150.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            WeatherDetailItem(
                icon = R.drawable.humidity,
                value = formatNumberBasedOnLanguage("${weather.main.humidity}") + "%",
                label = stringResource(R.string.humidity)
            )
            WeatherDetailItem(
                icon = R.drawable.wind,
                value = formatNumberBasedOnLanguage("${weather.wind.speed}") + windUnit,
                label = stringResource(R.string.wind)
            )
            WeatherDetailItem(
                icon = R.drawable.pressure,
                value = formatNumberBasedOnLanguage("${weather.main.pressure}") + stringResource(R.string.hpa),
                label = stringResource(R.string.pressure)
            )
            WeatherDetailItem(
                icon = R.drawable.cloudy,
                value = formatNumberBasedOnLanguage("${weather.clouds.all}"),
                label = stringResource(R.string.clouds)
            )
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeForecastScreen(navController: NavController, viewModel: WeatherViewModel) {
    val context = LocalContext.current
    val weatherState by viewModel.weatherState.collectAsState()
    val forecastState by viewModel.forecastState.collectAsState()
    val locationMethod by viewModel.locationMethod.collectAsStateWithLifecycle()
    val locationSetting = getLocationMethod(context)
    val windUnit = getWindSpeedUnit(context)

    LaunchedEffect(locationSetting) {
        if (locationSetting == "GPS") {
            LocationHelper(context).getFreshLocation { location ->
                val lang = loadLanguagePreference(context)
                val tempUnit = getTemperatureUnit(context, "Temp")
                if (location != null) {
                    viewModel.fetchWeather(location.latitude, location.longitude, lang, tempUnit, context)
                    viewModel.fetchWeatherForecast(location.latitude, location.longitude, lang, tempUnit, context)
                } else {
                    viewModel.fetchWeather(-0.13, 51.51, "en", "metric",context)
                    viewModel.fetchWeatherForecast(51.51, -0.13, "en", "metric", context)
                }
            }
        } else {
            val tempUnit = getTemperatureUnit(context, "Temp")
            val lang = loadLanguagePreference(context)
            viewModel.fetchWeather(
                viewModel.getSavedHomeLocation(context).longitude,
                viewModel.getSavedHomeLocation(context).latitude,
                lang,
                tempUnit,
                context
            )
            viewModel.fetchWeatherForecast(
                viewModel.getSavedHomeLocation(context).longitude,
                viewModel.getSavedHomeLocation(context).latitude,
                lang,
                tempUnit,
                context
            )
        }
    }

    // Network status handling
        if (!NetworkHelper.isNetworkAvailable(context)) {
            Toast.makeText(
                context,
                stringResource(R.string.this_is_the_last_data_when_you_are_connected_to_the_internet),
                Toast.LENGTH_SHORT
            ).show()
        } else if (shouldShowNetworkToast(context)) {
            Toast.makeText(context, "Network Available", Toast.LENGTH_SHORT).show()
        }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(android.graphics.Color.parseColor("#022a9a")),
                        Color(android.graphics.Color.parseColor("#5381ff"))
                    )
                )
            )
    ) {
        when (weatherState) {
            is Response.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.White
                )
            }

            is Response.Failure -> {
                val error = (weatherState as Response.Failure).error
                Text(
                    text = error.message ?: stringResource(R.string.unknown_error),
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )
                LaunchedEffect(error) {
                    Toast.makeText(context, error.message, Toast.LENGTH_SHORT).show()
                }
            }

            is Response.Success -> {
                val weather = (weatherState as Response.Success<WeatherResponse>).data
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // City Name
                    Text(
                        text = weather.name ?: "Unknown",
                        fontSize = 30.sp,
                        color = Color.White,
                        modifier = Modifier.padding(top = 48.dp)
                    )

                    // Weather Image
                    Image(
                        painter = painterResource(id = getWeatherIcon(weather.weather.firstOrNull()?.main)),
                        contentDescription = stringResource(R.string.weather_icon),
                        modifier = Modifier
                            .size(150.dp)
                            .padding(top = 8.dp)
                    )

                    // Weather Description
                    Text(
                        text = weather.weather.firstOrNull()?.description ?: stringResource(R.string.no_description),
                        fontSize = 19.sp,
                        color = Color.White,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    // Temperature
                    val formattedTemp = formatNumberBasedOnLanguage(weather.main.temp.toInt().toString())
                    val formattedUnit = formatTemperatureUnitBasedOnLanguage(
                        getTemperatureUnit(context, "Temp") ?: "C",
                        getTemperatureUnit(context, "Lang") ?: "en"
                    )
                    Text(
                        text = "$formattedTemp $formattedUnit",
                        fontSize = 65.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    // Weather Details Row
                    WeatherDetailsRow(weather, getTemperatureUnit(context, "Lang") ?: "en", windUnit)

                    // Handle forecast state separately
                    when (forecastState) {
                        is Response.Loading -> {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .padding(16.dp)
                                    .align(Alignment.CenterHorizontally),
                                color = Color.White
                            )
                        }

                        is Response.Failure -> {
                            val forecastError = (forecastState as Response.Failure).error
                            Text(
                                text = forecastError.message ?: "Forecast data unavailable",
                                color = Color.Red,
                                modifier = Modifier.padding(16.dp)
                            )
                        }

                        is Response.Success -> {
                            val forecast = (forecastState as Response.Success<WeatherForecastResponse>).data

                            // Today's Forecast
                            Text(
                                text = stringResource(R.string.today),
                                fontSize = 20.sp,
                                color = Color.White,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp, vertical = 8.dp)
                            )

                            LazyRow(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(forecast.list.take(6)) { item ->
                                    FutureModelViewHolder(item)
                                }
                            }

                            // Weekly Forecast
                            Text(
                                text = stringResource(R.string.weekly_forecast),
                                fontSize = 20.sp,
                                color = Color.White,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 24.dp, vertical = 8.dp)
                            )

                            WeeklyForecast(forecast)
                        }
                    }
                }
            }
        }
    }
}