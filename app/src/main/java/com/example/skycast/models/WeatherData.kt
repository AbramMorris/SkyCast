package com.example.skycast.models

import com.google.android.gms.maps.model.LatLng

data class WeatherData(
    val city: String,
    val temperature: Int,
    val condition: String,
    val storm: Int,
    val lat: Double,
    val lon: Double
){
    val latLng: LatLng
        get() = LatLng(lat, lon)
}