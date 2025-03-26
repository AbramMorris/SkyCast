package com.example.skycast.database

import androidx.room.Entity

@Entity(tableName = "saved_locations", primaryKeys = ["latitude", "longitude"])
data class SavedLocation(
    var name: String,
    var latitude: Double,
    var longitude: Double
)
