package com.example.skycast.UiI.Screen

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.skycast.Model.FutureModelViewHolder
import com.example.skycast.Model.WeatherDetailItem
import com.example.skycast.Model.WeatherForecastResponse
import com.example.skycast.Model.WeatherResponse
import com.example.skycast.Model.hourlyItems
import com.example.skycast.R
import com.example.skycast.Remote.WeatherApiServes
import com.example.skycast.ViewModel.WeatherViewModel
import com.example.skycast.ViewModel.WeatherViewModelFactory
import com.example.skycast.ui.theme.BlueBlackBack




@Composable
fun HomeForecastScreen(navController: NavController, viewModel: WeatherViewModel, ) {
    val viewModel: WeatherViewModel = viewModel(factory = WeatherViewModelFactory( WeatherApiServes.create()))
    val weatherState by viewModel.weatherState.collectAsState()
    val forecastState by viewModel.forecastState.collectAsState()
    val isLoading by viewModel.loading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val key = "6d0017f68dd3859d46f1f479f8cac002"

    LaunchedEffect(Unit) {
        viewModel.fetchWeather("London", key)
        viewModel.fetchWeatherForecast(51.51, -0.13, key)
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
                text = errorMessage ?: "Unknown error",
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
                    // City Name
                    Text(
                        text = weather.name ?: "Unknown",
                        fontSize = 30.sp,
                        color = Color.White,
                        modifier = Modifier.padding(top = 48.dp)
                    )

                    // Weather Image
                    Image(
                        painter = painterResource(id =
                        if(weather.weather.firstOrNull()?.main == "Clear")
                            R.drawable.sunny
                        else
                        R.drawable.cloudy_sunny),
                        contentDescription = "Weather Icon",
                        modifier = Modifier
                            .size(150.dp)
                            .padding(top = 8.dp)
                    )

                    // Weather Description
                    Text(
                        text = weather.weather.firstOrNull()?.description ?: "No description",
                        fontSize = 19.sp,
                        color = Color.White,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    // Temperature
                    Text(
                        text = "${weather.main.temp}°C",
                        fontSize = 65.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(top = 8.dp)
                    )

                    // Weather Details Row
                    WeatherDetailsRow(weather)

                    // Today's Forecast
                    Text(
                        text = "Today",
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
                        forecastState?.list?.let { hourlyForecast ->
                            items(hourlyForecast.take(6)) { item ->
                                FutureModelViewHolder(item)
                            }
                        }
                    }

                    // Weekly Forecast
                    Text(
                        text = "Weekly Forecast",
                        fontSize = 20.sp,
                        color = Color.White,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 8.dp)
                    )

                    WeeklyForecast(forecastState)
                }
            }
        }
    }
}
@Composable
fun WeatherDetailsRow(weather: WeatherResponse) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .background(
                BlueBlackBack,
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            WeatherDetailItem(icon = R.drawable.rain, value = "${weather.main.humidity}%", label = "Humidity")
            WeatherDetailItem(icon = R.drawable.wind, value = "${weather.wind.speed} km/h", label = "Wind")
            WeatherDetailItem(icon = R.drawable.humidity, value = "${weather.main.pressure} hPa", label = "Pressure")
        }
    }
}
@Composable
fun FutureModelViewHolder(forecast: WeatherForecastResponse.ForecastItem) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.cloudy_sunny),
            contentDescription = "Weather Icon",
            modifier = Modifier.size(50.dp)
        )
        Text(
            text = "${forecast.main.temp}°C",
            color = Color.White,
            fontSize = 16.sp
        )
        Text(
            text = forecast.dt_txt.substring(11, 16), // Extracts time
            color = Color.LightGray,
            fontSize = 14.sp
        )
    }
}
@Composable
fun WeeklyForecast(forecastState: WeatherForecastResponse?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        forecastState?.list?.chunked(8)?.take(5)?.forEachIndexed { index, dailyData ->
            val avgTemp = dailyData.map { it.main.temp }.average().toInt()
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .padding(vertical = 10.dp)
                    .background(BlueBlackBack, shape = RoundedCornerShape(10.dp))
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Day ${index + 1}", color = Color.White)
                Text("$avgTemp°C", color = Color.White)
                Image(
                    painter = painterResource(id = R.drawable.rain),
                    contentDescription = "Weather Icon"
                )
            }
        }
    }
}













//
//@Composable
//fun HomeForecastScreen(navController: NavController, viewModel: WeatherViewModel) {
//    val weather = viewModel.weatherState
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
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(top = 48.dp)
//                .verticalScroll(rememberScrollState()),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Text(
//                text = "Hello",
//                fontSize = 30.sp,
//                color = Color.White
//            )
//
//            Image(
//                painter = painterResource(id = R.drawable.cloudy_sunny),
//                contentDescription = "sun",
//                modifier = Modifier
//                    .size(150.dp)
//                    .padding(top = 8.dp)
//            )
//
//            Text(
//                text = "",
//                fontSize = 19.sp,
//                color = Color.White,
//                modifier = Modifier.padding(top = 8.dp)
//            )
//
//            Text(
//                text = "25°",
//                fontSize = 65.sp,
//                fontWeight = FontWeight.Bold,
//                color = Color.White,
//                modifier = Modifier.padding(top = 8.dp)
//            )
//
//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 24.dp, vertical = 16.dp)
//                    .background(
//                        BlueBlackBack,
//                        shape = RoundedCornerShape(16.dp)
//                    )
//            ) {
//                Row(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(100.dp)
//                        .padding(horizontal = 8.dp),
//                    verticalAlignment = Alignment.CenterVertically,
//                    horizontalArrangement = Arrangement.SpaceBetween
//                ) {
//                    WeatherDetailItem(icon = R.drawable.rain, value = "85%", label = "Rain")
//                    WeatherDetailItem(icon = R.drawable.wind, value = "10 km/h", label = "Wind")
//                    WeatherDetailItem(
//                        icon = R.drawable.humidity,
//                        value = "78%",
//                        label = "Humidity"
//                    )
//                }
//            }
//
//            Text(
//                text = "Today",
//                fontSize = 20.sp,
//                color = Color.White,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 24.dp, vertical = 8.dp)
//            )
//
//            LazyRow(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 20.dp),
//                horizontalArrangement = Arrangement.spacedBy(16.dp)
//            ) {
//                items(hourlyItems) { item ->
//                    FutureModelViewHolder(item)
//                }
//            }
//            Text(
//                text = "Weekly Forecast",
//                fontSize = 20.sp,
//                color = Color.White,
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 24.dp, vertical = 8.dp)
//            )
//
//            Column(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(10.dp)
//            ) {
//                repeat(5) { index ->
//                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .height(80.dp)
//                            .padding(vertical = 10.dp)
//                            .background(BlueBlackBack, shape = RoundedCornerShape(10.dp))
//                            .padding(20.dp),
//                        horizontalArrangement = Arrangement.SpaceBetween
//                    ) {
//                        Text("Day ${index + 1}", color = Color.White)
//                        Text("${weather.value }°C", color = Color.White)
//                        Image(
//                            painter = painterResource(id = R.drawable.rain),
//                            contentDescription = "Weather"
//                        )
//                    }
//                }
//            }
//        }
//    }
//}
