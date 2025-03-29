package com.example.skycast.data.models

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromDailyForecastList(value: List<WeatherForecastResponse>): String {
        return gson.toJson(value)
    }

    @TypeConverter
    fun toDailyForecastList(value: String): List<WeatherForecastResponse> {
        val type = object : TypeToken<List<WeatherForecastResponse>>() {}.type
        return gson.fromJson(value, type)
    }
    @TypeConverter
    fun fromWeatherList(value: List<WeatherResponse>): String {
        return gson.toJson(value)
    }
    @TypeConverter
    fun toWeatherList(value: String): List<WeatherResponse> {
        val type = object : TypeToken<List<WeatherResponse>>() {}.type
        return gson.fromJson(value, type)

    }
}