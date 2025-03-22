package com.example.skycast.UiI.Screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.skycast.Model.WeatherData
import com.example.skycast.R
import com.example.skycast.ViewModel.WeatherViewModel
@Composable
fun LocationsScreen(navController: NavController, viewModel: WeatherViewModel) {
    val locations = listOf(
        WeatherData("Montreal, Canada", 8, "Snowy", R.drawable.snowy),
        WeatherData("Tokyo, Japan", 12, "Thunderstorm", R.drawable.storm),
        WeatherData("Taipei, Taiwan", 20, "Cloudy", R.drawable.cloudy),
        WeatherData("Toronto, Canada", 12, "Tornado", R.drawable.windy)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E1E2E))
            .padding(16.dp)
    ) {
        Text("Pick Location", fontSize = 24.sp, color = Color.White)

        LazyColumn {
            items(locations) { location ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .background(Color(0xFF2A2D43), shape = RoundedCornerShape(10.dp))
                        .clickable {
//                            viewModel.fetchWeather( , )
                            navController.popBackStack()
                        }
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("location.city", color = Color.White, fontSize = 18.sp)
                        Text("${"location.temperature"}°C", color = Color.White)
                    }
                    Image(painter = painterResource(id = R.drawable.rain), contentDescription = "Weather Icon")
                }
            }
        }
    }
}
