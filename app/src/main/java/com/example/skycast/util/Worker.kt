package com.example.skycast.util


import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.skycast.data.database.FavDataBase.AppDatabase
import com.example.skycast.data.database.FavDataBase.LocalDataSource
import com.example.skycast.data.mapper.toResponse
import com.example.skycast.data.models.Response
import com.example.skycast.data.models.WeatherResponse
import com.example.skycast.data.remotes.WeatherApiServes
import com.example.skycast.data.remotes.WeatherRemoteDataSourceImpl
import com.example.skycast.data.repo.WeatherRepositoryImpl
import kotlinx.coroutines.flow.firstOrNull

class AlarmWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        Log.d("AlarmWorker", "doWork() started")

        return try {
            val apiService = WeatherApiServes.create()
            val remoteDataSource = WeatherRemoteDataSourceImpl(apiService)
            val localDataSource = LocalDataSource(AppDatabase.getDatabase(applicationContext).locationDao())
            val repository = WeatherRepositoryImpl(remoteDataSource, localDataSource)
            val tempUnit = getTemperatureUnit(applicationContext, "Temp") ?: "metric"
            val lang = getTemperatureUnit(applicationContext, "Lang") ?: "en"

            val currentWeather: Response<WeatherResponse> =
                repository.getCurrentWeather(30.0444, 31.2357, lang, tempUnit).firstOrNull()!!.toResponse()

            when (currentWeather) {
                is Response.Success -> {
                    val weatherData = currentWeather.data
                    val alarmIntent = Intent("com.example.weathersync.ALARM_TRIGGER").apply {
                        setPackage(applicationContext.packageName)
                        putExtra("temperature", weatherData.main.temp)
                        putExtra("description", weatherData.weather.firstOrNull()?.description)
                        putExtra("humidity", weatherData.main.humidity)
                        putExtra("currentTemperatureUnit", tempUnit)
                    }
                    Log.d("AlarmWorker", "Sending broadcast: $alarmIntent")
                    applicationContext.sendBroadcast(alarmIntent)
                    Log.d("AlarmWorker", "Alarm triggered successfully")
                    return Result.success()
                }

                is Response.Failure -> {
                    Log.e("AlarmWorker", "Failed to fetch weather data: ${currentWeather.error}")
                    return Result.failure()
                }

                is Response.Loading -> {
                    Log.d("AlarmWorker", "Weather data is still loading...")
                    return Result.retry()
                }
            }
        } catch (e: Exception) {
            Log.e("AlarmWorker", "Error in doWork()", e)
            return Result.failure()
        }
    }
}
