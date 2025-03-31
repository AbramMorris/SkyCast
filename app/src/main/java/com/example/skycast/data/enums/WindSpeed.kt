package com.example.skycast.data.enums

enum class WindSpeed( val displayName: String, val arabDisplayName : String, val code: String) {
    METERS_PER_SECOND("m/s", "م/ث", "metric"),
    MILES_PER_HOUR("mph", "م/س", "imperial");

}