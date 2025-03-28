package com.example.skycast.ui.screens

import android.location.Geocoder
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.skycast.R
import com.example.skycast.data.models.SavedLocation
import com.example.skycast.viewmodel.WeatherViewModel
import com.example.skycast.ui.theme.BlueLight
import com.example.skycast.ui.navigation.ScreenRoute
import com.example.skycast.util.MAP_KEY
import com.example.skycast.util.getLatLngFromCity
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
import kotlinx.coroutines.launch
import java.util.Locale






@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FavouriteLocationScreen(navController: NavController, viewModel: WeatherViewModel) {

    val locations by viewModel.savedLocations.collectAsState(emptyList())
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    LaunchedEffect(Unit) {

    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate(ScreenRoute.MapWithMarkers.route) },
                containerColor = Color.White
            ) {
                Icon(imageVector = Icons.Default.Favorite, contentDescription = "Add", tint = BlueLight)
            }
        },
        snackbarHost = {
        SnackbarHost(snackbarHostState)
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
                .padding(paddingValues)
        ) {
            Text(
                text = stringResource(R.string.favourite_location),
                fontSize = 24.sp,
                color = Color.White
            )

            LazyColumn {
                items(locations.sortedBy { it.name }, key = { it.latitude to it.longitude }) { location ->
                    val dismissState = rememberDismissState(
                        confirmStateChange = {
                            if (it == DismissValue.DismissedToStart || it == DismissValue.DismissedToEnd) {
                                coroutineScope.launch {
                                    viewModel.removeLocation(location)
                                    val result = snackbarHostState.showSnackbar(
                                        message = context.getString(R.string.location_deleted),
                                        actionLabel = context.getString(R.string.undo),
                                        duration = androidx.compose.material3.SnackbarDuration.Short
                                    )
                                    if (result == androidx.compose.material3.SnackbarResult.ActionPerformed) {
//                                        viewModel.addLocation(location)
                                        viewModel.insertFavLocation(location.name,location.latitude,location.longitude)
                                    }
                                }
                            }
                            true
                        }
                    )

                    SwipeToDismiss(
                        state = dismissState,
                        directions = setOf(DismissDirection.StartToEnd, DismissDirection.EndToStart),
                        background = {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(10.dp)
                                    .background(Color.Red, shape = RoundedCornerShape(10.dp))
                                    .padding(16.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = stringResource(R.string.delete),
                                    tint = Color.White
                                )
                            }
                        },
                        dismissContent = {
                            LocationItem(
                                location = location, onClick = { navController.popBackStack() },
                                navController = navController
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun LocationItem(location: SavedLocation, onClick: () -> Unit, navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .background(BlueLight, shape = RoundedCornerShape(10.dp))
            .clickable {   navController.navigate("${ScreenRoute.Details.route}/${location.latitude}/${location.longitude}") }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(location.name, color = Color.White, fontSize = 18.sp)
//            Text("Lat: ${location.latitude}, Lng: ${location.longitude}", color = Color.White)
        }
        Icon(imageVector = Icons.Default.LocationOn, contentDescription = stringResource(R.string.location_icon), tint = Color.White)
    }
}



@Composable
fun MapSelectionScreen(viewModel: WeatherViewModel, navController: NavController) {
    val context = LocalContext.current
    val selectedLocation by viewModel.selectedLocation.collectAsStateWithLifecycle()
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(20.0, 0.0), 3f) // Default world view
    }
    var loc : MutableState<LatLng> = remember { mutableStateOf(LatLng( 0.0,0.0 )) }
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
                    val locationName = addresses?.firstOrNull()?.getAddressLine(0) ?: context.getString(R.string.unknown_location)
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

                            selectedLocation?.let { viewModel.insertFavLocation( it.first,it.second,it.third) }
                            navController.popBackStack()
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


@Composable
fun Map(address: MutableState<String>, latLng: MutableState<LatLng>, viewModel: WeatherViewModel){
    val context = LocalContext.current
    Places.initializeWithNewPlacesApiEnabled(context,MAP_KEY)
    val placesClient = Places.createClient(context)
    var searchText by remember { mutableStateOf("") }
    var predictions by remember { mutableStateOf(emptyList<AutocompletePrediction>()) }
    val currentPojo  = viewModel.fetchWeather(latLng.value.longitude,latLng.value.latitude,"en","metric")
    val forecastPojo = viewModel.fetchWeatherForecast(latLng.value.latitude,latLng.value.longitude,"en","metric")
//    val pojo = SavedLocation("",0.0,0.0, currentPojo, forecastPojo )


    LaunchedEffect(searchText) {
            val response = placesClient.awaitFindAutocompletePredictions {
                this.query = searchText
                typesFilter= listOf(PlaceTypes.CITIES)
            }
            predictions = response.autocompletePredictions
    }
            PlacesAutocompleteTextField(
                modifier = Modifier.fillMaxWidth(),
                searchText = searchText,
                predictions = predictions.map { it.toPlaceDetails() },
                onQueryChanged = { searchText = it },
                onSelected = { autocompletePlace : AutocompletePlace ->
                    address.value = autocompletePlace.primaryText.toString()
                    latLng.value = getLatLngFromCity(context,address.value)!!
//                    viewModel.insertFavLocation(address.value,latLng.value.latitude,latLng.value.longitude)
//                    Log.i("loccccc", "lat = ${latLng.value.latitude}")
                },
            )
}

