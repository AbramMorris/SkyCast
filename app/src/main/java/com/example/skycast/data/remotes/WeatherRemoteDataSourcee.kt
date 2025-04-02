package com.example.skycast.data.remotes

import com.example.skycast.data.models.WeatherForecastResponse
import com.example.skycast.data.models.WeatherResponse
import retrofit2.Response

interface WeatherRemoteDataSourcee {
    suspend fun getCurrentWeather(latitude:Double, longitude :Double, lang: String ,unit : String): Response<WeatherResponse>
    suspend fun getWeatherForecast(lat: Double, lon: Double, lang: String ,unit:String): Response<WeatherForecastResponse>
}