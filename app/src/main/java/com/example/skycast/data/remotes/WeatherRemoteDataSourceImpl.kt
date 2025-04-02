package com.example.skycast.data.remotes

import android.util.Log
import com.example.skycast.data.models.WeatherForecastResponse
import com.example.skycast.data.models.WeatherResponse
import retrofit2.Response

class WeatherRemoteDataSourceImpl(private val apiService: WeatherApiServes) :
    WeatherRemoteDataSourcee {
    override suspend fun getCurrentWeather( latitude: Double, longitude: Double, lang: String,unit :String): Response<WeatherResponse> {
        Log.i("unit","unit = $unit")
        return apiService.getWeather(latitude, longitude, lang ,unit )
    }

    override suspend fun getWeatherForecast(lat: Double, lon: Double, lang: String,unit :String): Response<WeatherForecastResponse> {
        return apiService.getWeatherForecast(lat, lon, lang,unit)
    }
}