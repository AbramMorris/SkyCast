package com.example.skycast.util

import android.content.Context
import android.location.Geocoder
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import java.util.Locale

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
fun getLatLngFromCity(context: Context, cityName: String): LatLng? {
    return try {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses = geocoder.getFromLocationName(cityName, 1)
        addresses?.firstOrNull()?.let {
            LatLng(it.latitude, it.longitude)
        }
    } catch (e: Exception) {
        Log.e("Geocoder", "Error getting LatLng for $cityName: ${e.message}")
        null
    }
}
