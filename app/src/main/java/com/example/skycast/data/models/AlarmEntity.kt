package com.example.skycast.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarms")
data class AlarmEntity(
    @PrimaryKey val id: Int,
    val hour: Int,
    val minute: Int,
    var label: String,
    var latitude: Double,
    var longitude: Double
)