package com.example.skycast.repo

import android.util.Log
import com.example.skycast.database.LocalDataSource
import com.example.skycast.database.SavedLocation
import com.example.skycast.models.WeatherForecastResponse
import com.example.skycast.models.WeatherResponse
import com.example.skycast.remotes.WeatherRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

interface WeatherRepository {
    fun getCurrentWeather( long :Double, lat :Double,unit:String ): Flow<Result<WeatherResponse>>
    fun getWeatherForecast(lat: Double, lon: Double ,unit:String): Flow<Result<WeatherForecastResponse>>
    fun getAllLocations(): Flow<List<SavedLocation>>
    suspend fun insertLocation(location: SavedLocation)
    suspend fun deleteLocation(location: SavedLocation)
}

class WeatherRepositoryImpl(private val remoteDataSource: WeatherRemoteDataSource ,private val localDataSource: LocalDataSource) :
    WeatherRepository {
    private val _savedLocations = MutableStateFlow<List<SavedLocation>>(emptyList())
    val savedLocations = _savedLocations.asStateFlow()

    override fun getCurrentWeather( long :Double, lat :Double,unit: String): Flow<Result<WeatherResponse>> = flow {
        Log.i("unitRepo","unit = $unit")
        emit(Result.success(remoteDataSource.getCurrentWeather(long, lat,unit).body()!!))
    }.catch { e ->
        emit(Result.failure(e))
    }

    override fun getWeatherForecast(lat: Double, lon: Double,unit: String): Flow<Result<WeatherForecastResponse>> = flow {
        emit(Result.success(remoteDataSource.getWeatherForecast(lat, lon ,unit).body()!!))
    }.catch { e ->
        emit(Result.failure(e))
    }
    override fun getAllLocations(): Flow<List<SavedLocation>> {
        return localDataSource.getAllLocations()
    }

    override suspend fun insertLocation(location: SavedLocation) {
        localDataSource.insertLocation(location)
    }

    override suspend fun deleteLocation(location: SavedLocation) {
        localDataSource.deleteLocation(location)
    }

}