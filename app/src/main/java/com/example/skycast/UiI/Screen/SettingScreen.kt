package com.example.skycast.UiI.Screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.skycast.ViewModel.WeatherViewModel

@Composable
fun SettingsScreen( navController: NavController, viewModel: WeatherViewModel) {
    var selectedTemperature by remember { mutableStateOf("°C") }
    var selectedWindSpeed by remember { mutableStateOf("m/s") }
    var selectedPressure by remember { mutableStateOf("mbar") }
    var selectedTheme by remember { mutableStateOf("System") }
    var isNewDesignEnabled by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {
        // Top bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = { /* Handle back navigation */ }) {
                Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
            }
            Text(
                text = "Settings",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Log in section
        Card(
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Weather in favorite places on all your devices", fontSize = 16.sp)
                Button(onClick = { /* Handle log in */ }) {
                    Text(text = "Log in")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Temperature
        SettingSection(title = "Temperature", options = listOf("°C", "°F"), selectedOption = selectedTemperature) {
            selectedTemperature = it
        }

        // Wind Speed
        SettingSection(title = "Wind speed", options = listOf("m/s", "km/h", "mph", "knots"), selectedOption = selectedWindSpeed) {
            selectedWindSpeed = it
        }

        // Pressure
        SettingSection(title = "Pressure", options = listOf("mmHg", "mbar", "hPa", "inHg"), selectedOption = selectedPressure) {
            selectedPressure = it
        }

        // Theme
        SettingSection(title = "Theme", options = listOf("System", "Light", "Dark"), selectedOption = selectedTheme) {
            selectedTheme = it
        }

        // New design toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Star, contentDescription = "New design")
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "New design", fontSize = 18.sp)
            }
            Switch(checked = isNewDesignEnabled, onCheckedChange = { isNewDesignEnabled = it })
        }
    }
}

@Composable
fun SettingSection(title: String, options: List<String>, selectedOption: String, onOptionSelected: (String) -> Unit) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Medium)
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
                        containerColor = if (option == selectedOption) Color.Black else Color.LightGray,
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
