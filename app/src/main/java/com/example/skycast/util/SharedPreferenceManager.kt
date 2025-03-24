package com.example.skycast.util

import android.content.Context
import com.example.skycast.viewmodel.Temperature
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

//class SharedPreferenceManager(context: Context , value :String , key :String ) {



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
//private val _selectedTemperatureUnit = MutableStateFlow(sharedPref.getTemperatureUnit())
//val selectedTemperatureUnit: StateFlow<String> = _selectedTemperatureUnit

private val _displayedTemperature = MutableStateFlow("")
val displayedTemperature: StateFlow<String> = _displayedTemperature

//fun updateTemperatureUnit(newUnit: String, currentTemp: Double) {
//    val fromUnit = mapUnit(_selectedTemperatureUnit.value)
//    val toUnit = mapUnit(newUnit)
//
//    val convertedTemp = convertTemperature(currentTemp, fromUnit, toUnit)
//    sharedPref.saveTemperatureUnit(newUnit)
//
//    _selectedTemperatureUnit.value = newUnit
//    _displayedTemperature.value = "$convertedTemp$newUnit"
//}

private fun mapUnit(unit: String): String {
    return when (unit) {
        "°C" -> "celsius"
        "°F" -> "fahrenheit"
        "K" -> "kelvin"
        else -> "celsius"
    }
}
