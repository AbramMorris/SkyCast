package com.example.skycast.Remote

import com.example.skycast.Model.WeatherForecastResponse
import com.example.skycast.Model.WeatherResponse
import retrofit2.Response

class WeatherRemoteDataSourceImpl(private val apiService: WeatherApiServes) : WeatherRemoteDataSource {
    override suspend fun getCurrentWeather( latitude: Double, longitude: Double): Response<WeatherResponse> {
        return apiService.getWeather( latitude, longitude )
    }

    override suspend fun getWeatherForecast(lat: Double, lon: Double, apiKey: String): Response<WeatherForecastResponse> {
        return apiService.getWeatherForecast(lat, lon, apiKey)
    }
}