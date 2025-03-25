package com.example.skycast.uiI.screens

import android.location.Geocoder
import android.util.Log
import android.widget.Toast
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.skycast.BuildConfig
import com.example.skycast.models.WeatherData
import com.example.skycast.R
import com.example.skycast.database.SavedLocation
import com.example.skycast.viewmodel.WeatherViewModel
import com.example.skycast.ui.theme.BlueLight
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import org.checkerframework.checker.units.qual.C
import java.util.Locale
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun FavouriteLocationScreen(navController: NavController, viewModel: WeatherViewModel) {
    val locations = listOf(
        WeatherData("Montreal, Canada", 8, "Snowy", R.drawable.snowy, 51.1,52.2),
        WeatherData("Tokyo, Japan", 12, "Thunderstorm", R.drawable.storm,51.1,52.2),
        WeatherData("Taipei, Taiwan", 20, "Cloudy", R.drawable.cloudy,51.1,52.2),
        WeatherData("Toronto, Canada", 12, "Tornado", R.drawable.windy,51.1,52.2)
    )

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {      navController.navigate("map_with_markers")        }, containerColor = Color.White) {
                Icon(imageVector = Icons.Default.Favorite, contentDescription = "Add", tint = BlueLight)
            }
        }
    ) { paddingValues ->
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
                .padding(paddingValues) // Ensures content avoids FAB overlap
        ) {
            Text(
                text = stringResource(R.string.favourite_location),
                fontSize = 24.sp,
                color = Color.White
            )

            LazyColumn {
                items(locations) { location ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                            .background(BlueLight, shape = RoundedCornerShape(10.dp))
                            .clickable {
                                // viewModel.fetchWeather( , )
                                navController.popBackStack()
                            }
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(location.city, color = Color.White, fontSize = 18.sp)
                            Text("${location.temperature}°C", color = Color.White)
                        }
                        Image(
                            painter = painterResource(id = location.storm),
                            contentDescription = "Weather Icon"
                        )
                    }
                }
            }
        }
    }


}
@Composable
fun MapWithMarkers(viewModel: WeatherViewModel) {
    val context = LocalContext.current
    val savedLocations by viewModel.savedLocations.collectAsState()

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(0.0, 0.0), 2f)
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        onMapClick = { latLng ->
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            val locationName = addresses?.firstOrNull()?.getAddressLine(0) ?: "Unknown Location"

            val newLocation = SavedLocation(locationName, latLng.latitude, latLng.longitude)
            viewModel.addLocation(newLocation)
        }
    ) {
        savedLocations.forEach { location ->
            Marker(
                state = MarkerState(position = LatLng(location.latitude, location.longitude)),
                title = location.name,
                snippet = "Lat: ${location.latitude}, Lng: ${location.longitude}"
            )
        }
    }
}



//
//@Composable
//fun MapWithMarkers(locations: List<WeatherData>) {
//    val cameraPositionState = rememberCameraPositionState {
//        position = CameraPosition.fromLatLngZoom(locations.first().latLng, 4f)
//    }
//
//    GoogleMap(
//        modifier = Modifier.fillMaxSize(),
//        cameraPositionState = cameraPositionState
//    ) {
//        locations.forEach { location ->
//            Marker(
//                state = MarkerState(position = location.latLng),
//                title = location.city,
//                snippet = "Weather: ${location.condition}"
//            )
//        }
//    }
//}
//
//@Composable
//fun MapWithMarkers(locations: List<WeatherData>) {
//    val cameraPositionState = rememberCameraPositionState {
//        position = CameraPosition.fromLatLngZoom(locations.first().latLng, 4f)
//    }
//
//    GoogleMap(
//        modifier = Modifier.fillMaxSize(),
//        cameraPositionState = rememberCameraPositionState {
//            position = CameraPosition.fromLatLngZoom(LatLng(37.7749, -122.4194), 10f)
//        }
//    ) {
//        Marker(
//            position = LatLng(37.7749, -122.4194), // Example: San Francisco
//            title = "San Francisco",
//            snippet = "A cool place!"
//        )
//    }
//}

@Composable
fun Map(){
    val context = LocalContext.current
    Places.initializeWithNewPlacesApiEnabled(context,"AIzaSyCWWYJtCejwVw1rfb7WjiMYTc23XtQF2SQ")
    val placesClient = Places.createClient(context)
    val searchTextFlow = MutableStateFlow("")
    val searchText by searchTextFlow.collectAsStateWithLifecycle()
    var predictions by remember { mutableStateOf(emptyList<AutocompletePrediction>()) }

    LaunchedEffect(Unit) {
        searchTextFlow.debounce(500.milliseconds).collect { query : String ->
            val response = placesClient.awaitFindAutocompletePredictions {
                this.query = query
            }
            predictions = response.autocompletePredictions
        }
    }



            PlacesAutocompleteTextField(
                modifier = Modifier.fillMaxSize(),
                searchText = searchText,
                predictions = predictions.map { it.toPlaceDetails() },
                onQueryChanged = { searchTextFlow.value = it },
                onSelected = { autocompletePlace : AutocompletePlace ->
                    Log.i("mapppp","lat = ${autocompletePlace.latLng?.latitude}")
                },
            )



}


