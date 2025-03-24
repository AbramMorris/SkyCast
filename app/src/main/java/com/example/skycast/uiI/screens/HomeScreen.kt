package com.example.skycast.uiI.screens

import android.os.Build
import android.util.Log
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.skycast.models.WeatherDetailItem
import com.example.skycast.models.WeatherForecastResponse
import com.example.skycast.models.WeatherResponse
import com.example.skycast.R
import com.example.skycast.remotes.WeatherApiServes
import com.example.skycast.remotes.WeatherRemoteDataSourceImpl
import com.example.skycast.repo.WeatherRepositoryImpl
import com.example.skycast.util.LocationHelper
import com.example.skycast.viewmodel.WeatherViewModel
import com.example.skycast.viewmodel.WeatherViewModelFactory
import com.example.skycast.viewmodel.getDayNameFromDate
import com.example.skycast.ui.theme.BlueBlackBack
import com.example.skycast.util.getTemperatureUnit


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HomeForecastScreen(navController: NavController, viewModel: WeatherViewModel, ) {
    val apiService = WeatherApiServes.create() // Initialize Retrofit API service
    val remoteDataSource = WeatherRemoteDataSourceImpl(apiService) // Create Remote Data Source
    val repository = WeatherRepositoryImpl(remoteDataSource) // Create Repository
    val viewModelFactory = WeatherViewModelFactory(repository)
    val viewModel: WeatherViewModel = viewModel(factory = WeatherViewModelFactory( repository))
    val weatherState by viewModel.weatherState.collectAsState()
    val forecastState by viewModel.forecastState.collectAsState()
    val isLoading by viewModel.loading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val key = "6d0017f68dd3859d46f1f479f8cac002"
    val context = LocalContext.current
    var locationHelper = LocationHelper(context)
//    val sharedPref = remember { SharedPreferenceManager(context) }
//    val temperatureUnit = remember { sharedPref.getTemperatureUnit() }



    LaunchedEffect(Unit) {
        locationHelper.getFreshLocation { location ->
            if (location != null) {
                viewModel.fetchWeather(location.latitude, location.longitude , getTemperatureUnit(context,"Temp")
                    ?:"metric" )
                viewModel.fetchWeatherForecast(location.latitude, location.longitude,
                    getTemperatureUnit(context,"Temp") ?:"metric")
            }
            else{
                viewModel.fetchWeather(-0.13,51.51 ,"metric")
                viewModel.fetchWeatherForecast(51.51, -0.13,"metric")
            }
        }
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
                    // City Name
                    Text(
                        text = weather.name ?: "Unknown",
                        fontSize = 30.sp,
                        color = Color.White,
                        modifier = Modifier.padding(top = 48.dp)
                    )
                    Log.d("TAG", "HomeForecastScreen: ${weather.name}")

                    // Weather Image
                    Image(
                        painter = painterResource(id =
                        when(weather.weather.firstOrNull()?.main){
                            "Clear" -> R.drawable.sunny
                            "Clouds" -> R.drawable.cloudy
                            "Rain" -> R.drawable.rain
                            "Snow" -> R.drawable.snowy
                            "Thunderstorm" -> R.drawable.storm
                            "Drizzle" -> R.drawable.rain
                            "Mist" -> R.drawable.cloudy
                            else -> {
                                R.drawable.cloudy_sunny
                            }
                        })
                            ,
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
                    Text(
                        text = "${weather.main.temp.toInt()}°C",
                        fontSize = 65.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(top = 8.dp)
                    )
//                    Text(
//                        text = "Current Temperature: ${viewModel.displayedTemperature}",
//                        fontSize = 18.sp,
//                        fontWeight = FontWeight.Bold,
//                        color = Color.White
//                    )

                    // Weather Details Row
                    WeatherDetailsRow(weather)

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
                        forecastState?.list?.let { hourlyForecast ->
                            items(hourlyForecast.take(6)) { item ->
                                FutureModelViewHolder(item)
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
            WeatherDetailItem(icon = R.drawable.humidity, value = "${weather.main.humidity}%", label = "Humidity")
            WeatherDetailItem(icon = R.drawable.wind, value = "${weather.wind.speed} km/h", label = "Wind")
            WeatherDetailItem(icon = R.drawable.pressure, value = "${weather.main.pressure} hPa", label = "Pressure")
        }
    }
}
@Composable
fun FutureModelViewHolder(forecast: WeatherForecastResponse.ForecastItem) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = when(
                forecast.weather.firstOrNull()?.main
            ){
                "Clear" -> R.drawable.sunny
                "Clouds" -> R.drawable.cloudy
                "Rain" -> R.drawable.rain
                "Snow" -> R.drawable.snowy
                "Thunderstorm" -> R.drawable.storm
                "Drizzle" -> R.drawable.rain
                "Mist" -> R.drawable.cloudy
                else -> {
                    R.drawable.cloudy_sunny
                }
            }),
            contentDescription = stringResource(R.string.weather_icon),
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


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WeeklyForecast(forecastState: WeatherForecastResponse? ) {
    forecastState?.let { forecast ->
        val dailyAverages = forecast.list
            .groupBy { it.dt_txt.substring(0, 10) } // Group by date (YYYY-MM-DD)
            .mapValues { (_, dailyData) ->
                val avgTemp = dailyData.map { it.main.temp }.average().toInt()
                val weatherIcon = dailyData.firstOrNull()?.weather?.firstOrNull()?.icon
                avgTemp to weatherIcon
            }
            .toList()
            .take(5) // Show only 5 days

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {
            dailyAverages.forEachIndexed { index, (date, data) ->
                val (avgTemp, iconRes) = data
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .padding(vertical = 10.dp)
                        .background(BlueBlackBack, shape = RoundedCornerShape(10.dp))
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text( getDayNameFromDate(date) , color = Color.White
                    , fontSize = 18.sp
                        ,modifier = Modifier.padding(end = 10.dp)
                            , fontStyle =  androidx.compose.ui.text.font.FontStyle.Italic
                        , fontWeight = FontWeight.Bold
                    )
                    Text("$avgTemp°C", color = Color.White)

                    Image(
                        painter = painterResource(id =
                        when(iconRes){
                             "01d" -> R.drawable.sunny
                            "02d" -> R.drawable.cloudy
                             "10d" -> R.drawable.rain
                            else -> {
                                R.drawable.cloudy_sunny
                            }
                        }
                        ),
                        contentDescription = stringResource(R.string.weather_icon)
                    )
                }
            }
        }
    }
}




enum class Weather(val icon : Int) {
    CLOUDY_SUNNY(icon = R.drawable.cloudy_sunny),
    WIND(icon = R.drawable.wind),
    CLOUDY(icon = R.drawable.cloudy),
    WINDY(icon = R.drawable.windy),
    STORM(icon = R.drawable.storm),
    RAIN(icon = R.drawable.rain),
    SUNNY(icon = R.drawable.sunny),
    SNOWY(icon = R.drawable.snowy),
}

