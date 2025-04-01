package com.example.skycast.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.skycast.R
import com.example.skycast.data.enums.LanguageDisplay
import com.example.skycast.data.models.Response
import com.example.skycast.ui.navigation.ScreenRoute
import com.example.skycast.viewmodel.WeatherViewModel
import com.example.skycast.ui.theme.BlueLight
import com.example.skycast.util.getLocationMethod
import com.example.skycast.util.getTemperatureUnit
import com.example.skycast.util.getWindSpeedUnit
import com.example.skycast.util.loadLanguagePreference
import com.example.skycast.util.restartApp
import com.example.skycast.util.saveLanguagePreference
import com.example.skycast.util.saveLocationMethod
import com.example.skycast.util.saveTemperatureUnit
import com.example.skycast.util.saveWindSpeedUnit
import com.example.skycast.util.setLangSymbol

@Composable
fun SettingsScreen( navController: NavController, viewModel: WeatherViewModel) {

    val context = LocalContext.current
    var selectedTemperature by remember { mutableStateOf("°C") }
    selectedTemperature = getTemperatureUnit(context, "Temp")
    var selectedWindSpeed by remember { mutableStateOf("m/s") }
    selectedWindSpeed = getWindSpeedUnit(context)
    var selectedLocator by remember { mutableStateOf("System") }
    selectedLocator = getLocationMethod(context)
    var selectedLanguage by remember { mutableStateOf("en") }
    selectedLanguage = setLangSymbol(loadLanguagePreference(context))
    Log.d("save","new  $selectedLanguage")




    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(android.graphics.Color.parseColor("#022a9a")),
                        Color(android.graphics.Color.parseColor("#5381ff"))
                    )
                )
            )
            .padding(16.dp)
    ) {
        // Top bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
        ) {

            Text(
                text = stringResource(R.string.settings),
                fontSize = 22.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Temperature
        SettingSection(
            title = stringResource(R.string.temperature),
            options = listOf(stringResource(R.string.c), stringResource(R.string.f), stringResource(
                R.string.k
            )
            ),
            selectedOption = selectedTemperature
        ) { newSelection ->
            selectedTemperature = newSelection
            saveTemperatureUnit(context,"Temp",newSelection)
            Log.d("save","new selection $newSelection")
        }


        // Wind Speed
        SettingSection(title = stringResource(R.string.wind_speed),
            options = listOf(stringResource(R.string.m_s), stringResource(R.string.mph)),
            selectedOption = selectedWindSpeed)
        {
            newSelection ->
            selectedWindSpeed = newSelection
            saveWindSpeedUnit(context, newSelection)
        }

//         Language
        SettingSection(
            title = stringResource(R.string.language),
            options = listOf(LanguageDisplay.ARABIC.displayName, LanguageDisplay.ENGLISH.displayName),
            selectedOption = selectedLanguage
        ) { selectedOption ->
            selectedLanguage = selectedOption
            Log.d("save","new selection $selectedLanguage")
            saveTemperatureUnit(context,"Lang",selectedLanguage)
            if (selectedLanguage == LanguageDisplay.ENGLISH.displayName) {
                saveLanguagePreference(context, LanguageDisplay.ENGLISH.code)
                Log.d("save","new selection ${LanguageDisplay.ENGLISH.code}")
            } else if( selectedLanguage == LanguageDisplay.ARABIC.displayName){
                saveLanguagePreference(context, LanguageDisplay.ARABIC.code)
                Log.d("save","new selection ${LanguageDisplay.ARABIC.code}")
            }
            restartApp(context)
        }
        // Location
        SettingSection(
            title = stringResource(R.string.location),
            options = listOf(stringResource(R.string.gps), stringResource(R.string.map)),
            selectedOption = selectedLocator
        ) {
            selectedLocator = it
            viewModel.setLocationMethod(it)
            saveLocationMethod(context, it)
            if (it == "Map") {
                navController.navigate(ScreenRoute.SettingsMap.route)
            }
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
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp)
                ) {
                    Text(text = option)
                }
            }
        }
    }
}



//
//@Composable
//fun SettingsScreen(navController: NavController, viewModel: WeatherViewModel) {
//    val context = LocalContext.current
//
//    // Collect settings from ViewModel
//    val settingsState by viewModel.settingsState.collectAsState()
//    val message by viewModel.message.collectAsStateWithLifecycle()
//    val snackbarHostState = remember { SnackbarHostState() }
//
//    // Handle messages
//    LaunchedEffect(message) {
//        if (message.isNotEmpty()) {
//            snackbarHostState.showSnackbar(message)
//        }
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(
//                brush = Brush.verticalGradient(
//                    colors = listOf(
//                        Color(android.graphics.Color.parseColor("#022a9a")),
//                        Color(android.graphics.Color.parseColor("#5381ff"))
//                    )
//                )
//            )
//            .padding(16.dp)
//    ) {
//        // Top bar
//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(top = 20.dp)
//        ) {
//            Text(
//                text = stringResource(R.string.settings),
//                fontSize = 22.sp,
//                color = Color.White,
//                fontWeight = FontWeight.Bold,
//                modifier = Modifier.padding(start = 8.dp)
//            )
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        when (settingsState) {
//            is Response.Loading -> {
//                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                    CircularProgressIndicator(color = Color.White)
//                }
//            }
//
//            is Response.Failure -> {
//                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
//                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                        Text(
//                            text = stringResource(R.string.error_loading_settings),
//                            color = Color.Red
//                        )
//                        Button(
//                            onClick = { viewModel.loadSettings() },
//                            colors = ButtonDefaults.buttonColors(containerColor = BlueLight)
//                        ) {
//                            Text(text = stringResource(R.string.retry))
//                        }
//                    }
//                }
//            }
//
//            is Response.Success -> {
//                val settings = (settingsState as Response.Success<Settings>).data
//
//                // Temperature
//                SettingSection(
//                    title = stringResource(R.string.temperature),
//                    options = TemperatureUnit.values().map { stringResource(it.displayName) },
//                    selectedOption = stringResource(settings.temperatureUnit.displayName)
//                ) { newSelection ->
//                    val unit = TemperatureUnit.values().first {
//                        stringResource(it.displayName) == newSelection
//                    }
//                    viewModel.updateTemperatureUnit(unit)
//                }
//
//                // Wind Speed
//                SettingSection(
//                    title = stringResource(R.string.wind_speed),
//                    options = WindSpeedUnit.values().map { stringResource(it.displayName) },
//                    selectedOption = stringResource(settings.windSpeedUnit.displayName)
//                ) { newSelection ->
//                    val unit = WindSpeedUnit.values().first {
//                        stringResource(it.displayName) == newSelection
//                    }
//                    viewModel.updateWindSpeedUnit(unit)
//                }
//
//                // Language
//                SettingSection(
//                    title = stringResource(R.string.language),
//                    options = LanguageDisplay.values().map { it.displayName },
//                    selectedOption = settings.language.displayName
//                ) { newSelection ->
//                    val language = LanguageDisplay.values().first { it.displayName == newSelection }
//                    viewModel.updateLanguage(language)
//                }
//
//                // Location
//                SettingSection(
//                    title = stringResource(R.string.location),
//                    options = LocationMethod.values().map { stringResource(it.displayName) },
//                    selectedOption = stringResource(settings.locationMethod.displayName)
//                ) { newSelection ->
//                    val method = LocationMethod.values().first {
//                        stringResource(it.displayName) == newSelection
//                    }
//                    viewModel.updateLocationMethod(method)
//                    if (method == LocationMethod.MAP) {
//                        navController.navigate(ScreenRoute.SettingsMap.route)
//                    }
//                }
//            }
//        }
//    }
//}
//
//@Composable
//private fun SettingSection(
//    title: String,
//    options: List<String>,
//    selectedOption: String,
//    onOptionSelected: (String) -> Unit
//) {
//    Column(modifier = Modifier.padding(vertical = 8.dp)) {
//        Text(
//            text = title,
//            fontSize = 18.sp,
//            fontWeight = FontWeight.Medium,
//            color = Color.White
//        )
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .background(Color.White, shape = RoundedCornerShape(12.dp))
//                .padding(8.dp),
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            options.forEach { option ->
//                Button(
//                    onClick = { onOptionSelected(option) },
//                    colors = ButtonDefaults.buttonColors(
//                        containerColor = if (option == selectedOption) BlueLight else Color.LightGray,
//                        contentColor = if (option == selectedOption) Color.White else Color.Black
//                    ),
//                    shape = RoundedCornerShape(12.dp),
//                    modifier = Modifier
//                        .weight(1f)
//                        .padding(horizontal = 4.dp)
//                ) {
//                    Text(text = option)
//                }
//            }
//        }
//    }
//}