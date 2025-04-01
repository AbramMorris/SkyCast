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
import com.example.skycast.util.DrawableUtils.getWeatherIcon
import com.example.skycast.viewmodel.WeatherViewModel
import com.example.skycast.util.formatNumberBasedOnLanguage
import com.example.skycast.util.formatTemperatureUnitBasedOnLanguage
import com.example.skycast.util.getTemperatureUnit
import com.example.skycast.util.getWindSpeedUnit
import com.example.skycast.util.loadLanguagePreference
import com.example.skycast.util.setLangSymbol


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DetailsScreen(
    navController: NavController,
    latitude: Double,
    longitude: Double,
    viewModel: WeatherViewModel
) {
    val context = LocalContext.current
    val tempUnit = getTemperatureUnit(context, "Temp") ?: "metric"
    val windUnit = getWindSpeedUnit(context)
    val weatherState by viewModel.weatherState.collectAsState()
    val forecastState by viewModel.forecastState.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    LaunchedEffect(latitude, longitude) {
        val lang = loadLanguagePreference(context) ?: "en"
        viewModel.fetchWeather(latitude, longitude, lang, tempUnit)
        viewModel.fetchWeatherForecast(latitude, longitude, lang, tempUnit)
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
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = Color.White
            )
        } else if (errorMessage != null) {
            Text(
                text = errorMessage ?: stringResource(R.string.unknown_error),
                color = Color.Red,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            weatherState?.let { weather ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = weather.name ?: "Unknown",
                        fontSize = 30.sp,
                        color = Color.White,
                        modifier = Modifier.padding(top = 48.dp)
                    )

                    Image(
                        painter = painterResource(id = getWeatherIcon(weather.weather.firstOrNull()?.main)),
                        contentDescription = stringResource(R.string.weather_icon),
                        modifier = Modifier.size(150.dp).padding(top = 8.dp)
                    )

                    Text(
                        text = weather.weather.firstOrNull()?.description ?: stringResource(R.string.no_description),
                        fontSize = 19.sp,
                        color = Color.White,
                        modifier = Modifier.padding(top = 8.dp)
                    )

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

                    WeatherDetailsRow(weather, getTemperatureUnit(context, "Lang") ?: "en", windUnit)

                    Text(
                        text = stringResource(R.string.today),
                        fontSize = 20.sp,
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp)
                    )

                    LazyRow(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        forecastState?.list?.let { hourlyForecast ->
                            items(hourlyForecast.take(6)) { item ->
                                FutureModelViewHolder(item)
                            }
                        }
                    }

                    Text(
                        text = stringResource(R.string.weekly_forecast),
                        fontSize = 20.sp,
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp)
                    )

                    WeeklyForecast(forecastState)
                }
            }
        }
    }
}















//
//@RequiresApi(Build.VERSION_CODES.O)
//@Composable
//fun DetailsScreen(navController: NavController, latitude: Double, longitude: Double, viewModel: WeatherViewModel) {
//    val context = LocalContext.current
//    val tempUnit = getTemperatureUnit(context, "Temp") ?: "metric"
//    val windUnit = if (tempUnit == "imperial") "mph" else "m/s"
//
//    val weatherState by viewModel.weatherState.collectAsState()
//    val forecastState by viewModel.forecastState.collectAsState()
//    val isLoading by viewModel.loading.collectAsState()
//    val errorMessage by viewModel.errorMessage.collectAsState()
//
//    LaunchedEffect(latitude, longitude) {
//        val lang = getTemperatureUnit(context, "Lang") ?: "en"
//        viewModel.fetchWeather(latitude, longitude, lang, tempUnit)
//        viewModel.fetchWeatherForecast(latitude, longitude, lang, tempUnit)
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
//                    Text(
//                        text = weather.name ?: "Unknown",
//                        fontSize = 30.sp,
//                        color = Color.White,
//                        modifier = Modifier.padding(top = 48.dp)
//                    )
//
//                    Image(
//                        painter = painterResource(id = getWeatherIcon(weather.weather.firstOrNull()?.main)),
//                        contentDescription = stringResource(R.string.weather_icon),
//                        modifier = Modifier.size(150.dp).padding(top = 8.dp)
//                    )
//
//                    Text(
//                        text = weather.weather.firstOrNull()?.description ?: stringResource(R.string.no_description),
//                        fontSize = 19.sp,
//                        color = Color.White,
//                        modifier = Modifier.padding(top = 8.dp)
//                    )
//
//                    val formattedTemp = formatNumberBasedOnLanguage(weather.main.temp.toInt().toString())
//                    val formattedUnit = formatTemperatureUnitBasedOnLanguage(getTemperatureUnit(context, "Temp") ?: "C", getTemperatureUnit(context, "Lang") ?: "en")
//
//                    Text(
//                        text = "$formattedTemp $formattedUnit",
//                        fontSize = 65.sp,
//                        fontWeight = FontWeight.Bold,
//                        color = Color.White,
//                        modifier = Modifier.padding(top = 8.dp)
//                    )
//
//                    WeatherDetailsRow(weather, getTemperatureUnit(context, "Lang") ?: "en", windUnit)
//
//                    Text(
//                        text = stringResource(R.string.today),
//                        fontSize = 20.sp,
//                        color = Color.White,
//                        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp)
//                    )
//
//                    LazyRow(
//                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
//                        horizontalArrangement = Arrangement.spacedBy(16.dp)
//                    ) {
//                        forecastState?.list?.let { hourlyForecast ->
//                            items(hourlyForecast.take(6)) { item ->
//                                FutureModelViewHolder(item)
//                            }
//                        }
//                    }
//
//                    Text(
//                        text = stringResource(R.string.weekly_forecast),
//                        fontSize = 20.sp,
//                        color = Color.White,
//                        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp)
//                    )
//
//                    WeeklyForecast(forecastState)
//                }
//            }
//        }
//    }
//}

