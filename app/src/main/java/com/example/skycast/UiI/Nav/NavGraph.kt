package com.example.skycast.UiI.Nav

import android.annotation.SuppressLint
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.skycast.ScreenRoute
import com.example.skycast.UiI.Screen.HomeForecastScreen
import com.example.skycast.UiI.Screen.LocationsScreen
import com.example.skycast.UiI.Screen.SplashScreen
import com.example.skycast.ViewModel.WeatherViewModel
import com.example.skycast.ui.theme.BlueBlackBack
import com.exyte.animatednavbar.AnimatedNavigationBar
import com.exyte.animatednavbar.animation.balltrajectory.Parabolic
import com.exyte.animatednavbar.animation.indendshape.Height
import com.exyte.animatednavbar.animation.indendshape.shapeCornerRadius
import com.exyte.animatednavbar.utils.noRippleClickable
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.skycast.R
import com.example.skycast.Remote.WeatherApiServes
import com.example.skycast.ui.theme.BlueLight


@Composable
fun AppNavGraph(navController: NavHostController, viewModel: WeatherViewModel) {
    NavHost(navController = navController, startDestination = ScreenRoute.Splash.route) {
        composable(ScreenRoute.Splash.route) { SplashScreen(navController) }
        composable(ScreenRoute.Home.route) { HomeForecastScreen(navController, viewModel) }
        composable(ScreenRoute.Locations.route) { LocationsScreen(navController, viewModel) }
    }
}
@Composable
fun navBar(navController: NavHostController){
    val navigationItems = listOf(
        NavigationItem(
            title = ("home"),
            icon = Icons.Default.Home,
            route = ScreenRoute.Home
        ),
        NavigationItem(
            title = ("favourite"),
            icon = Icons.Default.Favorite,
            route = ScreenRoute.Locations
        ),
        NavigationItem(
            title = ("alert"),
            icon = Icons.Default.Notifications,
            route = ScreenRoute.Splash
        ),
        NavigationItem(
            title = ("setting"),
            icon = Icons.Default.Settings,
            route = ScreenRoute.Home
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




//
//@Composable
//fun BottomNavigationBar(navController: NavController, currentRoute: String?) {
//    val navigationBarItems = listOf(
//        NavigationItem(ScreenRoute.Home.route, NavigationBarItems.HOME.icon),
//        NavigationItem(ScreenRoute.Locations.route,NavigationBarItems.FAV.icon),
////        NavigationItem("profile", NavigationBarItems.PROFILE.icon)
//    )
//
////    val selectedIndex = navigationBarItems.indexOfFirst { it.route == currentRoute }.takeIf { it >= 0 } ?: 0
//    val selectedIndex = remember { mutableStateOf(0) }
//     val list = listOf(R.drawable.sunny,R.drawable.cloudy,R.drawable.rain,R.drawable.storm)
//    AnimatedNavigationBar(
//        modifier = Modifier.background(Color.DarkGray),
//        selectedIndex = selectedIndex.value,
//        cornerRadius = shapeCornerRadius( 34.dp),
//        ballAnimation = Parabolic(tween(300)),
//        indentAnimation = Height(tween(300)),
//        ballColor = Color.White,
//        barColor = Color.White,
//    ) {
//        navigationBarItems.forEachIndexed { index, item ->
//
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .noRippleClickable { navController.navigate(item.route) },
//            ) {
//                Icon(
//                    painter = painterResource(item.icon),
//                    contentDescription = item.route,
//                    modifier = Modifier.size(30.dp)
//
//                )
//            }
//        }
//    }
//}
//
//data class NavigationItem(val route: String, val icon: Int)
//
//enum class NavigationBarItems(val icon : Int){
//    HOME(icon = R.drawable.cloudy_sunny),
//    LOCATION(icon = R.drawable.wind),
//    FAV(icon = R.drawable.cloudy),
//    PROFILE(icon = R.drawable.windy)
//}
//
//
