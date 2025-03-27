package com.example.skycast.remotes




import com.example.skycast.models.WeatherForecastResponse
import com.example.skycast.models.WeatherResponse
import retrofit2.Response

interface WeatherRemoteDataSource {
    suspend fun getCurrentWeather(latitude:Double, longitude :Double, lang: String ,unit : String): Response<WeatherResponse>
    suspend fun getWeatherForecast(lat: Double, lon: Double, lang: String ,unit:String): Response<WeatherForecastResponse>
}