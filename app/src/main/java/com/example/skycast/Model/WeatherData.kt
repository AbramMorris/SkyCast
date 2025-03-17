package com.example.skycast.Model

data class WeatherData(
    val city: String,
    val temperature: Int,
    val condition: String,
    val storm: Int
)