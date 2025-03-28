package com.example.skycast.data.models

import androidx.room.Entity

@Entity(tableName = "saved_locations", primaryKeys = ["latitude", "longitude"])
data class SavedLocation(
    var name: String,
    var latitude: Double,
    var longitude: Double,
    var weatherPojo : List<WeatherResponse>,
    var forecastPojo : List<WeatherForecastResponse>,
)
