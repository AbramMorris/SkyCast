package com.example.skycast.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.skycast.ui.screens.HomeForecastScreen
import com.example.skycast.ui.screens.FavouriteLocationScreen
import com.example.skycast.ui.screens.SplashScreen
import com.example.skycast.viewmodel.WeatherViewModel
import com.example.skycast.ui.theme.BlueBlackBack
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.skycast.R
import com.example.skycast.ui.screens.SettingsScreen
import com.example.skycast.ui.theme.BlueLight
import com.example.skycast.ui.screens.DetailsScreen
import com.example.skycast.ui.screens.MapSelectionScreen
import com.example.skycast.viewmodel.AlarmViewModel


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavGraph(navController: NavHostController, viewModel: WeatherViewModel, alarmViewModel: AlarmViewModel) {
    NavHost(navController = navController, startDestination = ScreenRoute.Splash.route) {
        composable(ScreenRoute.Splash.route) { SplashScreen(navController) }
        composable(ScreenRoute.Home.route) { HomeForecastScreen(navController, viewModel) }
        composable(ScreenRoute.Locations.route) { FavouriteLocationScreen(navController, viewModel) }
        composable(ScreenRoute.Setting.route) { SettingsScreen(navController, viewModel) }
        composable(ScreenRoute.MapWithMarkers.route) { MapSelectionScreen( viewModel, navController) }
        composable(ScreenRoute.Details.route) { DetailsScreen(navController, 0.0, 0.0, viewModel) }
        composable(
            route = "${ScreenRoute.Details.route}/{latitude}/{longitude}",
            arguments = listOf(navArgument("latitude") { type = NavType.StringType }, navArgument("longitude") { type = NavType.StringType }))
        { backStackEntry ->
            val latitude = backStackEntry.arguments?.getString("latitude")?.toDoubleOrNull()
            val longitude = backStackEntry.arguments?.getString("longitude")?.toDoubleOrNull()
            if (latitude != null && longitude != null) {
                DetailsScreen(
                    navController = navController, latitude = latitude, longitude = longitude, viewModel = viewModel
                )
            }
        }
        composable(ScreenRoute.Alarm.route) {
            com.example.skycast.ui.screens.AlarmScreen( navController,alarmViewModel)
        }
        composable(ScreenRoute.AlarmBottons.route) {
            com.example.skycast.ui.screens.AlarmScreenUI( navController,alarmViewModel)
        }
        composable(ScreenRoute.AlarmMap.route) {
            com.example.skycast.ui.screens.AlarmMapScreen(viewModel, alarmViewModel, navController)
        }
    }
}

@Composable
fun navBar(navController: NavHostController){
    val navigationItems = listOf(
        NavigationItem(
            title = (stringResource(R.string.home)),
            icon = Icons.Default.Home,
            route = ScreenRoute.Home
        ),
        NavigationItem(
            title = (stringResource(R.string.favourite)),
            icon = Icons.Default.Favorite,
            route = ScreenRoute.Locations
        ),
        NavigationItem(
            title = (stringResource(R.string.alert)),
            icon = Icons.Default.Notifications,
            route = ScreenRoute.Alarm
        ),
        NavigationItem(
            title = (stringResource(R.string.setting)),
            icon = Icons.Default.Settings,
            route = ScreenRoute.Setting
        )
    )
    val selectedNavigationIndex = rememberSaveable {
        mutableIntStateOf(0)
    }

    NavigationBar (
        containerColor = Color.White
    ) {
        navigationItems.forEachIndexed { index, item ->
            NavigationBarItem(
                selected = selectedNavigationIndex.intValue == index,
                onClick = {
                    selectedNavigationIndex.intValue = index
                    navController.navigate(item.route.route)
                },
                icon = {
                    Icon(imageVector = item.icon, contentDescription = item.title)
                },
                label = {
                    Text(
                        item.title,
                        color = BlueBlackBack
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = BlueBlackBack,
                    indicatorColor = BlueLight
                    , unselectedIconColor = BlueBlackBack
                )

            )
        }
    }

}

data class NavigationItem(
    val title: String,
    val icon: ImageVector,
    val route: ScreenRoute
)