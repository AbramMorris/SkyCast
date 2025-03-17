package com.example.skycast.Model

data class WeatherForecastResponse(
    val city: City,
    val list: List<ForecastItem>, ){


    data class City(
        val coord: Coord,
        val country: String,
        val id: Int,
        val name: String,
        val population: Int,
        val sunrise: Int,
        val sunset: Int,
        val timezone: Int
    )

    data class ForecastItem(
        val clouds: Clouds,
        val dt: Int,
        val dt_txt: String,
        val main: Main,
        val pop: Double,
        val rain: Rain,
        val snow: Snow,
        val sys: Sys,
        val visibility: Int,
        val weather: List<Weather>,
        val wind: Wind
    )


    data class Rain(
        val `3h`: Double
    )

    data class Snow(
        val `3h`: Double
    )


}
