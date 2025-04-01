package com.example.skycast.util

import android.app.Activity
import android.content.Context
import android.location.Geocoder
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.work.WorkManager
import com.example.skycast.data.enums.LanguageDisplay
import com.example.skycast.data.enums.LocationLocator
import com.example.skycast.data.enums.TemperatureUnit
import com.example.skycast.data.enums.WindSpeed
import com.google.android.gms.maps.model.LatLng
import java.util.Locale

fun saveTemperatureUnit(context: Context, key: String, unit: String) {
    val englishUnit = mapTemperatureUnitToEnglish(unit) // Ensure it's saved in English
    val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    sharedPreferences.edit().putString(key, englishUnit).apply()
    Log.d("saveTemperatureUnit", "saveTemperatureUnit: $englishUnit")
}

fun getTemperatureUnit(context: Context, key: String): String {
    val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    val savedUnit = sharedPreferences.getString(key, TemperatureUnit.CELSIUS.displayName) ?: TemperatureUnit.CELSIUS.displayName

    return if (LanguageDisplay.ARABIC.code== loadLanguagePreference(context)) {
        mapTemperatureUnitToArabic(savedUnit)
    } else {
        mapTemperatureUnitToEnglish(savedUnit)
    }
}

fun convertWindSpeedWithTempUnit(context: Context, windSpeed: String) {
    val tempUnit = when (windSpeed) {
        WindSpeed.METERS_PER_SECOND.displayName -> TemperatureUnit.KELVIN.displayName
        WindSpeed.MILES_PER_HOUR.displayName -> TemperatureUnit.FAHRENHEIT.displayName
        else -> TemperatureUnit.CELSIUS.code
    }
    saveTemperatureUnit(context,"Temp",tempUnit)
    Log.d("saveWindSpeedUnit", "TempUnit: $tempUnit")
}

fun saveWindSpeedUnit(context: Context, unit: String) {
    val englishUnit = mapWindSpeedUnitToEnglish(unit)
    val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    sharedPreferences.edit().putString("Wind", unit).apply()
    convertWindSpeedWithTempUnit(context, englishUnit)
    Log.d("saveWindSpeedUnit", "saveWindSpeedUnit: $unit")
}

fun getWindSpeedUnit(context: Context): String {
    val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    return if (LanguageDisplay.ARABIC.code== loadLanguagePreference(context)) {
        mapWindSpeedUnitToArabic(sharedPreferences.getString("Wind", WindSpeed.METERS_PER_SECOND.displayName) ?: WindSpeed.METERS_PER_SECOND.displayName)
        } else {
        mapWindSpeedUnitToEnglish(sharedPreferences.getString("Wind", WindSpeed.METERS_PER_SECOND.displayName) ?: WindSpeed.METERS_PER_SECOND.displayName)

    }
}
fun mapWindSpeedUnitToArabic(windSpeed: String): String {
    return when(windSpeed){
        WindSpeed.METERS_PER_SECOND.displayName -> WindSpeed.METERS_PER_SECOND.arabDisplayName
        WindSpeed.MILES_PER_HOUR.displayName -> WindSpeed.MILES_PER_HOUR.arabDisplayName
        WindSpeed.METERS_PER_SECOND.arabDisplayName -> WindSpeed.METERS_PER_SECOND.arabDisplayName
        WindSpeed.MILES_PER_HOUR.arabDisplayName -> WindSpeed.MILES_PER_HOUR.arabDisplayName
        else -> {WindSpeed.METERS_PER_SECOND.arabDisplayName}
    }
}
fun mapWindSpeedUnitToEnglish(windSpeed: String): String {
    return when(windSpeed){
        WindSpeed.METERS_PER_SECOND.displayName -> WindSpeed.METERS_PER_SECOND.displayName
        WindSpeed.MILES_PER_HOUR.displayName -> WindSpeed.MILES_PER_HOUR.displayName
        WindSpeed.METERS_PER_SECOND.arabDisplayName -> WindSpeed.METERS_PER_SECOND.displayName
        WindSpeed.MILES_PER_HOUR.arabDisplayName -> WindSpeed.MILES_PER_HOUR.displayName
        else -> {WindSpeed.METERS_PER_SECOND.displayName}
    }
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
        TemperatureUnit.CELSIUS.displayName  -> TemperatureUnit.CELSIUS.displayName
        TemperatureUnit.FAHRENHEIT.displayName -> TemperatureUnit.FAHRENHEIT.displayName
        TemperatureUnit.KELVIN.displayName -> TemperatureUnit.KELVIN.displayName
        TemperatureUnit.CELSIUS.arabDisplayName  -> TemperatureUnit.CELSIUS.displayName
        TemperatureUnit.FAHRENHEIT.arabDisplayName -> TemperatureUnit.FAHRENHEIT.displayName
        TemperatureUnit.KELVIN.arabDisplayName -> TemperatureUnit.KELVIN.displayName
        else -> {TemperatureUnit.CELSIUS.displayName}
    }
}


fun setLangSymbol(language: String): String {
    return when(language){
        LanguageDisplay.ARABIC.code -> LanguageDisplay.ARABIC.displayName

        LanguageDisplay.ENGLISH.code -> LanguageDisplay.ENGLISH.displayName
        else ->  LanguageDisplay.ENGLISH.displayName
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
fun shouldShowNetworkToast(context: Context): Boolean {
    val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    val hasShownToast = sharedPreferences.getBoolean("network_toast_shown", false)

    if (!hasShownToast) {
        sharedPreferences.edit().putBoolean("network_toast_shown", true).apply()
        return true
    }
    return false
}
fun saveLocationMethod(context: Context, method: String)  {
    val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    sharedPreferences.edit().putString("location_method", method).apply()

}

fun getLocationMethod(context: Context): String {
    val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    val method = sharedPreferences.getString("location_method", "GPS") ?: "GPS"
    return if (LanguageDisplay.ARABIC.code== loadLanguagePreference(context)) {
        mapLocationLocatorToArabic(method)
    } else {
        mapLocationLocatorToEnglish(method)
    }
}
fun mapLocationLocatorToArabic(location: String): String {
    return when (location) {
        LocationLocator.GPS.displayName -> LocationLocator.GPS.arabDisplayName
        LocationLocator.MAP.displayName -> LocationLocator.MAP.arabDisplayName
        LocationLocator.GPS.arabDisplayName -> LocationLocator.GPS.arabDisplayName
        LocationLocator.MAP.arabDisplayName -> LocationLocator.MAP.arabDisplayName
        else -> {
            LocationLocator.GPS.arabDisplayName
        }
    }
}

    fun mapLocationLocatorToEnglish(location: String): String {
        return when (location) {
            LocationLocator.GPS.displayName -> LocationLocator.GPS.displayName
            LocationLocator.MAP.displayName -> LocationLocator.MAP.displayName
            LocationLocator.GPS.arabDisplayName -> LocationLocator.GPS.displayName
            LocationLocator.MAP.arabDisplayName -> LocationLocator.MAP.displayName
            else -> {
                LocationLocator.GPS.displayName
            }
        }
    }

    fun mapLocationLocator(location: String): String {
        return when (location) {
            LocationLocator.GPS.displayName -> LocationLocator.GPS.code
            LocationLocator.MAP.displayName -> LocationLocator.MAP.code
            LocationLocator.GPS.arabDisplayName -> LocationLocator.GPS.code
            LocationLocator.MAP.arabDisplayName -> LocationLocator.MAP.code
            else -> {
                LocationLocator.GPS.code
            }
        }
    }

