package com.example.skycast.ui.screens

import android.location.Geocoder
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.skycast.R
import com.example.skycast.data.models.AlarmEntity
import com.example.skycast.ui.theme.BlueLight
import com.example.skycast.util.MAP_KEY
import com.example.skycast.util.getLatLngFromCity
import com.example.skycast.viewmodel.AlarmViewModel
import com.example.skycast.viewmodel.WeatherViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.PlaceTypes
import com.google.android.libraries.places.api.net.kotlin.awaitFindAutocompletePredictions
import com.google.android.libraries.places.compose.autocomplete.components.PlacesAutocompleteTextField
import com.google.android.libraries.places.compose.autocomplete.models.AutocompletePlace
import com.google.android.libraries.places.compose.autocomplete.models.toPlaceDetails
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import java.util.Locale


@Composable
fun AlarmMapScreen(viewModel: WeatherViewModel, alarmViewModel: AlarmViewModel, navController: NavController) {
    val context = LocalContext.current
    val selectedLocation by viewModel.selectedLocation.collectAsStateWithLifecycle()
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(20.0, 0.0), 3f) // Default world view
    }
    var loc : MutableState<LatLng> = remember { mutableStateOf(LatLng( 0.0,0.0 )) }
    val alarmEntity by alarmViewModel.selectedAlarmLocation.collectAsStateWithLifecycle()
//    val currentPojo : List<WeatherResponse> = viewModel.fetchWeather(loc.value.longitude,loc.value.latitude,"en","metric").toList()
//    val forcastPojo = viewModel.fetchWeatherForecast(loc.value.latitude,loc.value.longitude,"en","metric").toList()

    val addreess = remember { mutableStateOf("") }
    LaunchedEffect(selectedLocation) {
        addreess.value = selectedLocation?.first ?: ""
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Place Autocomplete Search Bar
            Map(addreess,loc ,viewModel)

            // Google Map
            GoogleMap(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                cameraPositionState = cameraPositionState,
                onMapClick = { latLng ->
                    val geocoder = Geocoder(context, Locale.getDefault())
                    val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                    val locationName = addresses?.firstOrNull()?.getAddressLine(0) ?: context.getString(
                        R.string.unknown_location)
                    Log.i("mapppp", "lat = ${latLng.latitude}")
                    viewModel.updateSelectedLocation(locationName, latLng.latitude, latLng.longitude)
                }
            ) {
                selectedLocation?.let { location ->
                    Marker(
                        state = MarkerState(position = LatLng(location.second, location.third)),
                        title = location.first,
                        snippet = "Lat: ${location.second}, Lng: ${location.third}"
                    )
                }
            }
        }

        // Bottom Card with Save Button
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0D0F33))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = addreess.value, color = Color.White)
                }
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = {
                            if (selectedLocation != null) {
                                val cityName = selectedLocation!!.first
                                val lat = selectedLocation!!.second
                                val lng = selectedLocation!!.third

                                navController.previousBackStackEntry
                                    ?.savedStateHandle
                                    ?.set("selectedCity", cityName)
                                navController.previousBackStackEntry
                                    ?.savedStateHandle
                                    ?.set("selectedLat", lat)
                                navController.previousBackStackEntry
                                    ?.savedStateHandle
                                    ?.set("selectedLng", lng)

                                navController.popBackStack()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BlueLight),
                        modifier = Modifier.padding(bottom = 8.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(text = stringResource(R.string.save_location), color = Color.White)
                    }
                }
            }
        }
    }
}


//@Composable
//fun Map(address: MutableState<String>, latLng: MutableState<LatLng>, viewModel: WeatherViewModel){
//    val context = LocalContext.current
//    Places.initializeWithNewPlacesApiEnabled(context, MAP_KEY)
//    val placesClient = Places.createClient(context)
//    var searchText by remember { mutableStateOf("") }
//    var predictions by remember { mutableStateOf(emptyList<AutocompletePrediction>()) }
//    val currentPojo  = viewModel.fetchWeather(latLng.value.longitude,latLng.value.latitude,"en","metric")
//    val forecastPojo = viewModel.fetchWeatherForecast(latLng.value.latitude,latLng.value.longitude,"en","metric")
////    val pojo = SavedLocation("",0.0,0.0, currentPojo, forecastPojo )
//
//
//    LaunchedEffect(searchText) {
//        val response = placesClient.awaitFindAutocompletePredictions {
//            this.query = searchText
//            typesFilter= listOf(PlaceTypes.CITIES)
//        }
//        predictions = response.autocompletePredictions
//    }
//    PlacesAutocompleteTextField(
//        modifier = Modifier.fillMaxWidth(),
//        searchText = searchText,
//        predictions = predictions.map { it.toPlaceDetails() },
//        onQueryChanged = { searchText = it },
//        onSelected = { autocompletePlace : AutocompletePlace ->
//            address.value = autocompletePlace.primaryText.toString()
//            latLng.value = getLatLngFromCity(context,address.value)!!
////                    viewModel.insertFavLocation(address.value,latLng.value.latitude,latLng.value.longitude)
////                    Log.i("loccccc", "lat = ${latLng.value.latitude}")
//        },
//    )
//}
