package com.example.skycast.data.repo

import com.example.skycast.data.models.SavedLocation
import com.example.skycast.data.models.WeatherForecastResponse
import com.example.skycast.data.models.WeatherResponse
import kotlinx.coroutines.flow.Flow

interface WeatherRepository {
    fun getCurrentWeather( long :Double, lat :Double, lang: String,unit:String ): Flow<Result<WeatherResponse>>
    fun getWeatherForecast(lat: Double, lon: Double, lang: String ,unit:String): Flow<Result<WeatherForecastResponse>>
    fun getAllLocations(): Flow<List<SavedLocation>>
    suspend fun getLocationByCoordinates(lat: Double, lon: Double): SavedLocation?
    suspend fun insertLocation(location: SavedLocation)
    suspend fun deleteLocation(location: SavedLocation)

}