package com.example.skycast.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import androidx.navigation.NavController
import com.example.skycast.R
import com.example.skycast.data.models.Response
import com.example.skycast.data.models.SavedLocation
import com.example.skycast.util.DrawableUtils.getWeatherIcon
import com.example.skycast.viewmodel.WeatherViewModel
import com.example.skycast.util.formatNumberBasedOnLanguage
import com.example.skycast.util.formatTemperatureUnitBasedOnLanguage
import com.example.skycast.util.getTemperatureUnit
import com.example.skycast.util.getWindSpeedUnit
import com.example.skycast.util.loadLanguagePreference




@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DetailsScreen(
    navController: NavController,
    latitude: Double,
    longitude: Double,
    viewModel: WeatherViewModel
) {
    val context = LocalContext.current
    val tempUnit = getTemperatureUnit(context, "Temp")
    val windUnit = getWindSpeedUnit(context)
    val savedLocationsState by viewModel.savedLocationsState.collectAsState()
    val selectedLocation by viewModel.selectedLocation.collectAsState()

    LaunchedEffect(latitude, longitude) {
        viewModel.getFavLocation(latitude, longitude)
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
        when (savedLocationsState) {
            is Response.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color.White
                )
            }

            is Response.Failure -> {
                val error = (savedLocationsState as Response.Failure).error
                Text(
                    text = error.message ?: stringResource(R.string.unknown_error),
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            is Response.Success -> {
                val savedLocations = (savedLocationsState as Response.Success<List<SavedLocation>>).data
                val currentLocation = savedLocations.find {
                    it.latitude == latitude && it.longitude == longitude
                } ?: run {
                    // Show empty state if location not found
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text ="location_not_found",
                            color = Color.White,
                            fontSize = 20.sp
                        )
                        Text(
                            text = "${selectedLocation.first} (${selectedLocation.second}, ${selectedLocation.third})",
                            color = Color.White,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    return@Box
                }

                val weatherResponse = currentLocation.weatherPojo.firstOrNull()
                val forecastResponse = currentLocation.forecastPojo.firstOrNull()

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Location Name
                    Text(
                        text = weatherResponse?.name ?: selectedLocation.first,
                        fontSize = 30.sp,
                        color = Color.White,
                        modifier = Modifier.padding(top = 48.dp)
                    )

                    // Weather Icon and Description
                    weatherResponse?.weather?.firstOrNull()?.let { weather ->
                        Image(
                            painter = painterResource(id = getWeatherIcon(weather.main)),
                            contentDescription = stringResource(R.string.weather_icon),
                            modifier = Modifier.size(150.dp).padding(top = 8.dp)
                        )

                        Text(
                            text = weather.description ?: stringResource(R.string.no_description),
                            fontSize = 19.sp,
                            color = Color.White,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }

                    // Temperature
                    weatherResponse?.main?.let { main ->
                        val formattedTemp = formatNumberBasedOnLanguage(main.temp.toInt().toString())
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
                    }

                    // Weather Details Row
                    weatherResponse?.let {
                        WeatherDetailsRow(it, getTemperatureUnit(context, "Lang") ?: "en", windUnit = windUnit)
                    }

                    // Today's Forecast
                    Text(
                        text = stringResource(R.string.today),
                        fontSize = 20.sp,
                        color = Color.White,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 8.dp)
                    )

                    // Hourly Forecast
                    forecastResponse?.list?.let { hourlyForecast ->
                        LazyRow(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(hourlyForecast.take(6)) { item ->
                                FutureModelViewHolder( item)
                            }
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

                    forecastResponse?.let { forecast ->
                        WeeklyForecast(forecast)
                    }
                }
            }
        }
    }
}










//
//@RequiresApi(Build.VERSION_CODES.O)
//@Composable
//fun DetailsScreen(
//    navController: NavController,
//    latitude: Double,
//    longitude: Double,
//    viewModel: WeatherViewModel
//) {
//    val context = LocalContext.current
//    val tempUnit = getTemperatureUnit(context, "Temp")
//    val windUnit = getWindSpeedUnit(context)
//    val isLoading by viewModel.isLoading.collectAsState()
//    val errorMessage by viewModel.errorMessage.collectAsState()
//    val savedLocations by viewModel.savedLocations.collectAsState(emptyList())
//
//    val currentLocation = remember(savedLocations, latitude, longitude) {
//        savedLocations.find {
//            it.latitude == latitude && it.longitude == longitude
//        }
//    }
//
//    LaunchedEffect(latitude, longitude) {
//        viewModel.getFavLocation(latitude, longitude)
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
//        when {
//            isLoading -> {
//                CircularProgressIndicator(
//                    modifier = Modifier.align(Alignment.Center),
//                    color = Color.White
//                )
//            }
//            errorMessage != null -> {
//                Text(
//                    text = errorMessage ?: stringResource(R.string.unknown_error),
//                    color = Color.Red,
//                    modifier = Modifier.align(Alignment.Center)
//                )
//            }
//            currentLocation == null -> {
//                Text(
//                    text = "location_not_found",
//                    color = Color.White,
//                    modifier = Modifier.align(Alignment.Center)
//                )
//            }
//            else -> {
//                val weatherResponse = currentLocation.weatherPojo.firstOrNull()
//                Column(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .verticalScroll(rememberScrollState()),
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    // Location Name
//                    Text(
//                        text = weatherResponse?.name ?: "Unknown",
//                        fontSize = 30.sp,
//                        color = Color.White,
//                        modifier = Modifier.padding(top = 48.dp)
//                    )
//
//                    // Weather Icon
//                    weatherResponse?.weather?.firstOrNull()?.let { weather ->
//                        Image(
//                            painter = painterResource(id = getWeatherIcon(weather.main)),
//                            contentDescription = stringResource(R.string.weather_icon),
//                            modifier = Modifier.size(150.dp).padding(top = 8.dp)
//                        )
//
//                        // Weather Description
//                        Text(
//                            text = weather.description ?: stringResource(R.string.no_description),
//                            fontSize = 19.sp,
//                            color = Color.White,
//                            modifier = Modifier.padding(top = 8.dp)
//                        )
//                    }
//
//                    // Temperature
//                    weatherResponse?.main?.let { main ->
//                        val formattedTemp = formatNumberBasedOnLanguage(main.temp.toInt().toString())
//                        val formattedUnit = formatTemperatureUnitBasedOnLanguage(
//                            getTemperatureUnit(context, "Temp") ?: "C",
//                            getTemperatureUnit(context, "Lang") ?: "en"
//                        )
//
//                        Text(
//                            text = "$formattedTemp $formattedUnit",
//                            fontSize = 65.sp,
//                            fontWeight = FontWeight.Bold,
//                            color = Color.White,
//                            modifier = Modifier.padding(top = 8.dp)
//                        )
//                    }
//
//                    // Weather Details Row
//                    weatherResponse?.let {
//                        WeatherDetailsRow(it, getTemperatureUnit(context, "Lang") ?: "en", windUnit = windUnit)
//                    }
//
//                    // Today's Forecast
//                    Text(
//                        text = stringResource(R.string.today),
//                        fontSize = 20.sp,
//                        color = Color.White,
//                        modifier = Modifier.fillMaxWidth()
//                            .padding(horizontal = 24.dp, vertical = 8.dp)
//                    )
//
//                    // Hourly Forecast
//                    LazyRow(
//                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
//                        horizontalArrangement = Arrangement.spacedBy(16.dp)
//                    ) {
//                        currentLocation.forecastPojo.firstOrNull()?.list?.let { hourlyForecast ->
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
//                        modifier = Modifier.fillMaxWidth()
//                            .padding(horizontal = 24.dp, vertical = 8.dp)
//                    )
//
//                    currentLocation.forecastPojo.firstOrNull()?.let { forecast ->
//                        WeeklyForecast(forecast)
//                    }
//                }
//            }
//        }
//    }
//}
//
