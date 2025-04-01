package com.example.skycast.ui.navigation

sealed class ScreenRoute(val route: String) {
    object Home : ScreenRoute("home")
    object Locations : ScreenRoute("locations")
    object Splash : ScreenRoute("splash")
    object Setting : ScreenRoute("setting")
    object MapWithMarkers : ScreenRoute("map_with_markers")
    object Details : ScreenRoute("details")
    object Alarm : ScreenRoute("alarm")
    object AlarmBottons : ScreenRoute("alarm_buttons")
    object AlarmMap : ScreenRoute("alarm_map")
    object SettingsMap : ScreenRoute("settings_map")
}

