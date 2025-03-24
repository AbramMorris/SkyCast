package com.example.skycast.remotes




import com.example.skycast.models.WeatherForecastResponse
import com.example.skycast.models.WeatherResponse
import retrofit2.Response

interface WeatherRemoteDataSource {
    suspend fun getCurrentWeather(latitude:Double, longitude :Double ,unit : String): Response<WeatherResponse>
    suspend fun getWeatherForecast(lat: Double, lon: Double ,unit:String): Response<WeatherForecastResponse>
}