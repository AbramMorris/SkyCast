package com.example.skycast.Remote




import com.example.skycast.Model.WeatherForecastResponse
import com.example.skycast.Model.WeatherResponse
import retrofit2.Response

interface WeatherRemoteDataSource {
    suspend fun getCurrentWeather(latitude:Double, longitude :Double): Response<WeatherResponse>
    suspend fun getWeatherForecast(lat: Double, lon: Double, apiKey: String): Response<WeatherForecastResponse>
}