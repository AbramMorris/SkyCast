package com.example.skycast.uiI.navigation

sealed class ScreenRoute(val route: String) {
    object Home : ScreenRoute("home")
    object Locations : ScreenRoute("locations")
    object Splash : ScreenRoute("splash")
    object Setting : ScreenRoute("setting")
}

