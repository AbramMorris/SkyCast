package com.example.skycast.uiI.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material.icons.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.skycast.uiI.screens.HomeForecastScreen
import com.example.skycast.uiI.screens.FavouriteLocationScreen
import com.example.skycast.uiI.screens.SplashScreen
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
import com.example.skycast.R
import com.example.skycast.uiI.screens.SettingsScreen
import com.example.skycast.ui.theme.BlueLight
import com.example.skycast.uiI.screens.MapSelectionScreen


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavGraph(navController: NavHostController, viewModel: WeatherViewModel) {
    NavHost(navController = navController, startDestination = ScreenRoute.Splash.route) {
        composable(ScreenRoute.Splash.route) { SplashScreen(navController) }
        composable(ScreenRoute.Home.route) { HomeForecastScreen(navController, viewModel) }
        composable(ScreenRoute.Locations.route) { FavouriteLocationScreen(navController, viewModel) }
        composable(ScreenRoute.Setting.route) { SettingsScreen(navController, viewModel) }
        composable(ScreenRoute.MapWithMarkers.route) { MapSelectionScreen( viewModel, navController) }
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
            route = ScreenRoute.Splash
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