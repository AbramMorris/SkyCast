package com.example.skycast

sealed class ScreenRoute(val route: String) {
    object Home : ScreenRoute("home")
    object Locations : ScreenRoute("locations")
    object Splash : ScreenRoute("splash")
}

