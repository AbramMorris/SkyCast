package com.example.skycast.models

data class WeatherData(
    val city: String,
    val temperature: Int,
    val condition: String,
    val storm: Int
)