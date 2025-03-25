package com.example.skycast.uiI.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.skycast.viewmodel.WeatherViewModel
import com.example.skycast.ui.theme.BlueLight
import com.example.skycast.util.getTemperatureUnit
import com.example.skycast.util.saveTemperatureUnit

@Composable
fun SettingsScreen( navController: NavController, viewModel: WeatherViewModel) {
    val context = LocalContext.current


    var selectedTemperature by remember { mutableStateOf("°C") }
    selectedTemperature = getTemperatureUnit(context, "Temp").toString()
    var selectedWindSpeed by remember { mutableStateOf("m/s") }
    var selectedPressure by remember { mutableStateOf("mbar") }
    var selectedTheme by remember { mutableStateOf("System") }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .background( brush = Brush.verticalGradient(
                colors = listOf(
                    Color(android.graphics.Color.parseColor("#022a9a")),
                    Color(android.graphics.Color.parseColor("#5381ff"))
                )
            ))
            .padding(16.dp)
    ) {
        // Top bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
                .padding(top = 20.dp)
        ) {

            Text(
                text = "Settings",
                fontSize = 22.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Temperature
        SettingSection(
            title = "Temperature",
            options = listOf("°C", "°F", "K"),
            selectedOption = selectedTemperature
        ) { newSelection ->
            selectedTemperature = newSelection
            saveTemperatureUnit(context,"Temp",newSelection)
            Log.d("save","new selection $newSelection")
        }

        // Wind Speed
        SettingSection(title = "Wind speed", options = listOf("meter/sec", "miles/hour"), selectedOption = selectedWindSpeed) {
            selectedWindSpeed = it
        }

        // Language
        SettingSection(title = "language", options = listOf("Arabic", "English"), selectedOption = selectedPressure) {
            selectedPressure = it
        }

        // Location
        SettingSection(title = "location", options = listOf("GPS", "Map"), selectedOption = selectedTheme) {
            selectedTheme = it
        }

    }
}

@Composable
fun SettingSection(title: String, options: List<String>, selectedOption: String, onOptionSelected: (String) -> Unit) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Medium, color = Color.White)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, shape = RoundedCornerShape(12.dp))
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            options.forEach { option ->
                Button(
                    onClick = { onOptionSelected(option) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (option == selectedOption) BlueLight else Color.LightGray,
                        contentColor = if (option == selectedOption) Color.White else Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f).padding(horizontal = 4.dp)
                ) {
                    Text(text = option)
                }
            }
        }
    }
}
