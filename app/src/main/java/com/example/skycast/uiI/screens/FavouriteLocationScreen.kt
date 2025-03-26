package com.example.skycast.uiI.screens

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
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarResult
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.rememberDismissState
import androidx.compose.material.rememberScaffoldState
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
import com.example.skycast.database.SavedLocation
import com.example.skycast.viewmodel.WeatherViewModel
import com.example.skycast.ui.theme.BlueLight
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
    LaunchedEffect(Unit) {

    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("map_with_markers") },
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
                                        message = "Location deleted",
                                        actionLabel = "Undo",
                                        duration = androidx.compose.material3.SnackbarDuration.Short
                                    )
                                    if (result == androidx.compose.material3.SnackbarResult.ActionPerformed) {
                                        viewModel.addLocation(location)
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
                                    contentDescription = "Delete",
                                    tint = Color.White
                                )
                            }
                        },
                        dismissContent = {
                            LocationItem(location = location, onClick = { navController.popBackStack() })
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun LocationItem(location: SavedLocation, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
            .background(BlueLight, shape = RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(location.name, color = Color.White, fontSize = 18.sp)
//            Text("Lat: ${location.latitude}, Lng: ${location.longitude}", color = Color.White)
        }
        Icon(imageVector = Icons.Default.LocationOn, contentDescription = "Location Icon", tint = Color.White)
    }
}



@Composable
fun MapSelectionScreen(viewModel: WeatherViewModel, navController: NavController) {
    val uiState by viewModel.cityName.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val selectedLocation by viewModel.selectedLocation.collectAsStateWithLifecycle()
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(20.0, 0.0), 3f) // Default world view
    }
    var loc : MutableState<LatLng> = remember { mutableStateOf(LatLng( 0.0,0.0 )) }

//    LaunchedEffect(selectedLocation) {
//        latLng = selectedLocation
//    }

    val addreess = remember { mutableStateOf("") }
    LaunchedEffect(selectedLocation) {
        addreess.value = selectedLocation?.name ?: ""
    }
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Place Autocomplete Search Bar
            Map(addreess,loc ,viewModel)

            // Google Map
            GoogleMap(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f), // Let the map take the remaining space
                cameraPositionState = cameraPositionState,
                onMapClick = { latLng ->
                    val geocoder = Geocoder(context, Locale.getDefault())
                    val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                    val locationName = addresses?.firstOrNull()?.getAddressLine(0) ?: "Unknown Location"
                    Log.i("mapppp", "lat = ${latLng.latitude}")

                    viewModel.updateSelectedLocation(SavedLocation(locationName, latLng.latitude, latLng.longitude))
                }
            ) {
                selectedLocation?.let { location ->
                    Marker(
                        state = MarkerState(position = LatLng(location.latitude, location.longitude)),
                        title = location.name,
                        snippet = "Lat: ${location.latitude}, Lng: ${location.longitude}"
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

                            selectedLocation?.let { viewModel.addLocation(it) }
//                            latLng?.let { viewModel.addLocation(latLng!!) }
                            navController.popBackStack()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BlueLight),
                        modifier = Modifier.padding(bottom = 8.dp),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text(text = "Save Location", color = Color.White)
                    }
                }
            }
        }
    }
}


@Composable
fun Map(address: MutableState<String>, latLng: MutableState<LatLng>, viewModel: WeatherViewModel){
    val context = LocalContext.current
    Places.initializeWithNewPlacesApiEnabled(context,"AIzaSyCWWYJtCejwVw1rfb7WjiMYTc23XtQF2SQ")
    val placesClient = Places.createClient(context)
    var searchText by remember { mutableStateOf("") }
    var predictions by remember { mutableStateOf(emptyList<AutocompletePrediction>()) }
    var pojo : SavedLocation = SavedLocation("",0.0,0.0)


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
                    latLng.value = getLatLngFromCity(context, autocompletePlace.primaryText.toString().lowercase())!!
                    pojo.name = autocompletePlace.primaryText.toString()
                    pojo.latitude = latLng.value.latitude
                    pojo.longitude = latLng.value.longitude
                    viewModel.addLocation(pojo)
                    Log.i("loccccc", "lat = ${latLng.value.latitude}")
                },
            )
}

