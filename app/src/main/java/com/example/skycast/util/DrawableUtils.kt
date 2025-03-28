package com.example.skycast.util

import com.example.skycast.R

object DrawableUtils {

    fun getWeatherIcon(condition: String?): Int {
        return when (condition) {
            "Clear" -> R.drawable.sunny
            "Clouds" -> R.drawable.cloudy
            "Rain" -> R.drawable.rain
            "Snow" -> R.drawable.snowy
            "Thunderstorm" -> R.drawable.storm
            "Drizzle" -> R.drawable.rain
            "Mist" -> R.drawable.mist
            "Haze" -> R.drawable.haze
            "Fog" -> R.drawable.fog
            "Dust" -> R.drawable.dust
            "Sand" -> R.drawable.sand
            "Squall" -> R.drawable.squalls
            else -> R.drawable.cloudy_sunny
        }
    }
}