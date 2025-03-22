package com.example.skycast.Reposatory

import com.example.skycast.Model.WeatherForecastResponse
import com.example.skycast.Model.WeatherResponse
import com.example.skycast.Remote.WeatherRemoteDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow

interface WeatherRepository {
    fun getCurrentWeather( long :Double, lat :Double): Flow<Result<WeatherResponse>>
    fun getWeatherForecast(lat: Double, lon: Double, apiKey: String): Flow<Result<WeatherForecastResponse>>
}

class WeatherRepositoryImpl(private val remoteDataSource: WeatherRemoteDataSource) : WeatherRepository {

    override fun getCurrentWeather( long :Double, lat :Double): Flow<Result<WeatherResponse>> = flow {
        emit(Result.success(remoteDataSource.getCurrentWeather(long, lat).body()!!))
    }.catch { e ->
        emit(Result.failure(e))
    }

    override fun getWeatherForecast(lat: Double, lon: Double, apiKey: String): Flow<Result<WeatherForecastResponse>> = flow {
        emit(Result.success(remoteDataSource.getWeatherForecast(lat, lon, apiKey).body()!!))
    }.catch { e ->
        emit(Result.failure(e))
    }
}