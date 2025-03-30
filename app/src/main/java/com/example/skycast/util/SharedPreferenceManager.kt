package com.example.skycast.util

import android.app.Activity
import android.content.Context
import android.location.Geocoder
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.work.WorkManager
import com.example.skycast.R
import com.example.skycast.data.enums.LanguageDisplay
import com.example.skycast.data.enums.TemperatureUnit
import com.google.android.gms.maps.model.LatLng
import java.util.Locale

fun saveTemperatureUnit(context : Context, key :String , value: String) {
        val sharedPreferences = context.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)
//        if (LanguageDisplay.ARABIC.displayName == loadLanguagePreference(context)) {
//            sharedPreferences.edit().putString(key, value).apply()
//        }
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun getTemperatureUnit(context : Context, key: String): String? {
        val sharedPreferences = context.getSharedPreferences("weather_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString(key, "C")
    }


fun mapTemperatureUnit(temperature: String): String {
    return when(temperature){
        TemperatureUnit.CELSIUS.displayName  -> TemperatureUnit.CELSIUS.code
        TemperatureUnit.FAHRENHEIT.displayName -> TemperatureUnit.FAHRENHEIT.code
        TemperatureUnit.KELVIN.displayName -> TemperatureUnit.KELVIN.code
        TemperatureUnit.CELSIUS.arabDisplayName  -> TemperatureUnit.CELSIUS.code
        TemperatureUnit.FAHRENHEIT.arabDisplayName -> TemperatureUnit.FAHRENHEIT.code
        TemperatureUnit.KELVIN.arabDisplayName -> TemperatureUnit.KELVIN.code
        else -> TemperatureUnit.CELSIUS.code
    }
}

fun mapTemperatureUnitToArabic(temperature: String): String {
    return when(temperature){
        TemperatureUnit.CELSIUS.displayName  -> TemperatureUnit.CELSIUS.arabDisplayName
        TemperatureUnit.FAHRENHEIT.displayName -> TemperatureUnit.FAHRENHEIT.arabDisplayName
        TemperatureUnit.KELVIN.displayName -> TemperatureUnit.KELVIN.arabDisplayName
        else -> {TemperatureUnit.CELSIUS.arabDisplayName}
    }
}

fun mapTemperatureUnitToEnglish(temperature: String): String {
    return when(temperature){
        TemperatureUnit.CELSIUS.arabDisplayName  -> TemperatureUnit.CELSIUS.displayName
        TemperatureUnit.FAHRENHEIT.arabDisplayName -> TemperatureUnit.FAHRENHEIT.displayName
        TemperatureUnit.KELVIN.arabDisplayName -> TemperatureUnit.KELVIN.displayName
        else -> {TemperatureUnit.CELSIUS.displayName}
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
    return if (currentLang == LanguageDisplay.ARABIC.displayName) LanguageDisplay.ARABIC.code else LanguageDisplay.ENGLISH.code
}



fun formatNumberBasedOnLanguage(number: String): String {
    val arabicDigits = arrayOf('٠', '١', '٢', '٣', '٤', '٥', '٦', '٧', '٨', '٩')
    return if (Locale.getDefault().language == LanguageDisplay.ARABIC.code) {
        Log.d("name", "formatNumberBasedOnLanguage: ${Locale.getDefault().language}")
        number.map { if (it.isDigit()) arabicDigits[it.digitToInt()] else it }.joinToString("")
    } else {
        number
    }
}


fun formatTemperatureUnitBasedOnLanguage(unit: String, language: String): String {
    if (language == LanguageDisplay.ARABIC.displayName) {
        return when (unit) {
            TemperatureUnit.CELSIUS.arabDisplayName -> "°س"
            TemperatureUnit.FAHRENHEIT.arabDisplayName-> "°ف"
            TemperatureUnit.KELVIN.arabDisplayName -> "°ك"
            else -> "°س"
        }
    }
    return unit
}

fun restartApp(context: Context) {
    val intent = (context as Activity).intent
    context.finish()
    context.startActivity(intent)
}

fun loadLanguagePreference(context: Context): String {
    val sharedPreferences = context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
    return sharedPreferences.getString("LANGUAGE", "en") ?: "en"
}

fun saveLanguagePreference(context: Context, languageCode: String) {
    val sharedPreferences = context.getSharedPreferences("AppSettings", Context.MODE_PRIVATE)
    sharedPreferences.edit().putString("LANGUAGE", languageCode).apply()
}

fun getWindSpeedUnit(tempUnit: String): String {
    return if (tempUnit == "imperial") "mph" else "m/s"
}


enum class WindSpeedUnit(val displayName: String, val code: String) {
    METERS_PER_SECOND("m/s", "metric"),
    MILES_PER_HOUR("mph", "imperial");
}

fun cancelAlarmWorker(context: Context, alarmId: Int) {
    WorkManager.getInstance(context).cancelAllWorkByTag("alarm_$alarmId")
    Log.d("AlarmWorker", "AlarmWorker canceled for ID $alarmId")
}

fun isInternetAvailable(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val network = connectivityManager.activeNetwork ?: return false
    val capabilities =
        connectivityManager.getNetworkCapabilities(network) ?: return false
    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
}