package com.example.skycast.remotes

import com.example.skycast.models.WeatherForecastResponse
import com.example.skycast.models.WeatherResponse
import retrofit2.Response

class WeatherRemoteDataSourceImpl(private val apiService: WeatherApiServes) :
    WeatherRemoteDataSource {
    override suspend fun getCurrentWeather( latitude: Double, longitude: Double,unit :String): Response<WeatherResponse> {
        return apiService.getWeather(
            latitude, longitude,unit)
    }

    override suspend fun getWeatherForecast(lat: Double, lon: Double,unit :String): Response<WeatherForecastResponse> {
        return apiService.getWeatherForecast(lat, lon,unit)
    }
}