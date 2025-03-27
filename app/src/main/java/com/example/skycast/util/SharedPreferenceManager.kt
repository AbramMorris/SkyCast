package com.example.skycast.util

import android.app.Activity
import android.content.Context
import android.content.Intent
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
fun convertMetersPerSecondToMilesPerHour(speedInMetersPerSecond: Double): Double {
    return speedInMetersPerSecond * 2.23694
}

fun setLanguage(currentLang: String): String {
    return if (currentLang == "Arabic") "ar" else "en"
}



fun formatNumberBasedOnLanguage(number: String, language: String): String {
    val arabicDigits = arrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
    return if (language =="Arabic") {
        number.map { if (it.isDigit()) arabicDigits[it.digitToInt()] else it }.joinToString("")
    } else {
        number
    }
}

fun formatTemperatureUnitBasedOnLanguage(unit: String, language: String): String {
    if (language == "Arabic") {
        return when (unit) {
            "°C" -> "°س"
            "°F" -> "°ف"
            "°K" -> "°ك"
            else -> "°س"
        }
    }
    return unit
}
fun setAppLocale(context: Context, languageCode: String) {
    val locale = Locale(languageCode)
    Locale.setDefault(locale)

    val config = context.resources.configuration
    config.setLocale(locale)

    context.resources.updateConfiguration(config, context.resources.displayMetrics)

    // Save the language preference
    saveLanguagePreference(context, languageCode)
}

fun restartApp(context: Context) {
    val intent = (context as Activity).intent
    context.finish()
    context.startActivity(intent)
}

fun loadLanguagePreference(context: Context): String {
    val sharedPreferences = context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
    Log.d("loadLanguagePreference", "loadLanguagePreference: ${sharedPreferences.getString("LANGUAGE", "en")}")
    return sharedPreferences.getString("LANGUAGE", "en") ?: "en"
}

fun saveLanguagePreference(context: Context, languageCode: String) {
    val sharedPreferences = context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
    sharedPreferences.edit().putString("LANGUAGE", languageCode).apply()
}

enum class Languages(val displayName: String, val code: String) {
    ENGLISH("English", "en"),
    SPANISH("Español", "es"),
    FRENCH("Français", "fr"),
    GERMAN("Deutsch", "de"),
    ITALIAN("Italiano", "it"),
    CHINESE("中文", "zh"),
    JAPANESE("日本語", "ja"),
    ARABIC("العربية", "ar"),
    HINDI("हिन्दी", "hi"),
    PORTUGUESE("Português", "pt");
    companion object {
        fun fromCode(code: String): Languages? = Languages.entries.find { it.code == code }
        fun fromDisplayName(displayName: String): Languages? = Languages.entries.find { it.displayName == displayName }
    }
}