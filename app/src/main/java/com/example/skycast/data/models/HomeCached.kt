package com.example.skycast.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "home_cached")
data class HomeCached (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    var weatherPojo : List<WeatherResponse>,
    var forecastPojo : List<WeatherForecastResponse>
)