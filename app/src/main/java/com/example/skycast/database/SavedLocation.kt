package com.example.skycast.database

import androidx.room.Entity

@Entity(tableName = "saved_locations", primaryKeys = ["latitude", "longitude"])
data class SavedLocation(
    val name: String,
    val latitude: Double,
    val longitude: Double
)
