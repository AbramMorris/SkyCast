package com.example.skycast.data.enums

enum class TemperatureUnit(val displayName: String, val arabDisplayName : String, val code: String) {
    CELSIUS("°C", "°س","metric"),
    FAHRENHEIT("°F","°ف", "imperial"),
    KELVIN("°K","ك", "standard");
}
