package com.example.skycast.data.remotes

import com.example.skycast.data.models.WeatherForecastResponse
import com.example.skycast.data.models.WeatherResponse
import com.example.skycast.util.API_KEY
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApiServes {
    @GET("weather")
    suspend fun getWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("lang") lang: String,
        @Query("units") units: String,
        @Query("appid") apiKey: String= API_KEY

    ): Response<WeatherResponse>

    @GET("forecast")
    suspend fun getWeatherForecast(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("lang") lang: String,
        @Query("units") units: String,
        @Query("appid") apiKey: String= API_KEY
    ): Response<WeatherForecastResponse>

    companion object{
        private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"

        fun create(): WeatherApiServes {
            val retrofit = Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(BASE_URL)
                .build()
            return retrofit.create(WeatherApiServes::class.java)
        }
    }
}