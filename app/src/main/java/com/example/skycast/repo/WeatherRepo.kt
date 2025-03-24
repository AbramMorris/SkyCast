package com.example.skycast.repo

import android.util.Log
import com.example.skycast.models.WeatherForecastResponse
import com.example.skycast.models.WeatherResponse
import com.example.skycast.remotes.WeatherRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

interface WeatherRepository {
    fun getCurrentWeather( long :Double, lat :Double,unit:String ): Flow<Result<WeatherResponse>>
    fun getWeatherForecast(lat: Double, lon: Double ,unit:String): Flow<Result<WeatherForecastResponse>>
}

class WeatherRepositoryImpl(private val remoteDataSource: WeatherRemoteDataSource) :
    WeatherRepository {

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
}