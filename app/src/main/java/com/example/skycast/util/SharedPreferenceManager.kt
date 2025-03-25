package com.example.skycast.util

import android.content.Context

    fun saveTemperatureUnit(context : Context, key :String , value: String) {
        val sharedPreferences = context.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun getTemperatureUnit(context : Context, key: String): String? {
        val sharedPreferences = context.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString(key, "C")
    }


fun mapTemperatureUnit(temperature: String): String {
    return when(temperature){
        "°C" -> "metric"
        "°F" -> "imperial"
        "K" -> "standard"
        else -> "metric"
    }
}

fun setUnitSymbol(temperature: String): String {
    return when(temperature){
        "°C" -> "°C"
        "°F" -> "°F"
        "K" -> "K"
        else -> "°C"
    }
}
