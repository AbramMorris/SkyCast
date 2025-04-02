package com.example.skycast.viewmodel
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.skycast.R
import com.example.skycast.data.models.SavedLocation
import com.example.skycast.data.models.WeatherForecastResponse
import com.example.skycast.data.models.WeatherResponse
import com.example.skycast.data.repo.WeatherRepository
import com.example.skycast.util.mapTemperatureUnit
import com.example.skycast.util.setLanguage
import com.example.skycast.data.mapper.toList
import com.example.skycast.data.models.HomeCached
import com.example.skycast.data.models.Response
import com.example.skycast.data.repo.HomeCacheRepo
import com.example.skycast.util.isInternetAvailable
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale




class WeatherViewModel(
    private val repository: WeatherRepository,
    private val homeCacheRepo: HomeCacheRepo
) : ViewModel() {

    private val _weatherState = MutableStateFlow<Response<WeatherResponse>>(Response.Loading)
    val weatherState: StateFlow<Response<WeatherResponse>> = _weatherState.asStateFlow()

    private val _forecastState = MutableStateFlow<Response<WeatherForecastResponse>>(Response.Loading)
    val forecastState: StateFlow<Response<WeatherForecastResponse>> = _forecastState.asStateFlow()

    private val _savedLocations = MutableStateFlow<Response<List<SavedLocation>>>(Response.Loading)
    val savedLocationsState: StateFlow<Response<List<SavedLocation>>> = _savedLocations.asStateFlow()

    private val _locationMethod = MutableStateFlow("GPS")
    val locationMethod: StateFlow<String> = _locationMethod.asStateFlow()

    private val _selectedHomeLocation = MutableStateFlow<Triple<String, Double, Double>?>(null)
    val selectedHomeLocation: StateFlow<Triple<String, Double, Double>?> = _selectedHomeLocation.asStateFlow()

    private val _selectedLocation = MutableStateFlow(Triple("", 0.0, 0.0))
    val selectedLocation: StateFlow<Triple<String, Double, Double>> = _selectedLocation.asStateFlow()

    private val mutableMessage = MutableSharedFlow<String>()
    val message: SharedFlow<String> = mutableMessage.asSharedFlow()

        val savedLocations: Flow<List<SavedLocation>> = repository.getAllLocations()

    fun updateSelectedLocation(locationName: String, log: Double, lat: Double) {
        _selectedLocation.value = Triple(locationName, log, lat)
    }

    fun fetchWeather(long: Double, lat: Double, lang: String, unit: String, context: Context) {
        val newTemp = mapTemperatureUnit(unit)
        val newLang = lang

        viewModelScope.launch {
            _weatherState.value = Response.Loading

            if (isInternetAvailable(context)) {
                repository.getCurrentWeather(long, lat, newLang, newTemp).collect { result ->
                    result.fold(
                        onSuccess = { weather ->
                            _weatherState.value = Response.Success(weather)
                            cacheHomeData(weather, null)
                        },
                        onFailure = { error ->
                            _weatherState.value = Response.Failure(error)
                            fetchWeatherFromCache()
                        }
                    )
                }
            } else {
                fetchWeatherFromCache()
            }
        }
    }

    fun fetchWeatherForecast(lat: Double, lon: Double, lang: String, unit: String, context: Context) {
        val newTemp = mapTemperatureUnit(unit)
        val newLang = lang

        viewModelScope.launch {
            _forecastState.value = Response.Loading

            if (isInternetAvailable(context)) {
                repository.getWeatherForecast(lat, lon, newLang, newTemp).collect { result ->
                    result.fold(
                        onSuccess = { forecast ->
                            _forecastState.value = Response.Success(forecast)
                            cacheHomeData(null, forecast)
                        },
                        onFailure = { error ->
                            _forecastState.value = Response.Failure(error)
                            fetchForecastFromCache()
                        }
                    )
                }
            } else {
                fetchForecastFromCache()
            }
        }
    }

    private suspend fun fetchWeatherFromCache() {
        try {
            val cachedHome = homeCacheRepo.getHome()
            cachedHome?.weatherPojo?.firstOrNull()?.let { weather ->
                _weatherState.value = Response.Success(weather)
            } ?: run {
                _weatherState.value = Response.Failure(Exception("No cached weather data available"))
            }
        } catch (e: Exception) {
            _weatherState.value = Response.Failure(e)
        }
    }

    private suspend fun fetchForecastFromCache() {
        try {
            val cachedHome = homeCacheRepo.getHome()
            cachedHome?.forecastPojo?.firstOrNull()?.let { forecast ->
                _forecastState.value = Response.Success(forecast)
            } ?: run {
                _forecastState.value = Response.Failure(Exception("No cached forecast data available"))
            }
        } catch (e: Exception) {
            _forecastState.value = Response.Failure(e)
        }
    }

    fun getFavLocations() {
        viewModelScope.launch {
            _savedLocations.value = Response.Loading
            try {
                repository.getAllLocations().collect { locations ->
                    _savedLocations.value = Response.Success(locations.sortedBy { it.name })
                }
            } catch (e: Exception) {
                _savedLocations.value = Response.Failure(e)
            }
        }
    }

    fun getFavLocation(lat: Double, lon: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val location = repository.getLocationByCoordinates(lat, lon)
                location?.let {
                    _selectedLocation.value = Triple(it.name, it.longitude, it.latitude)
                    mutableMessage.emit("Location found in favorites")
                } ?: mutableMessage.emit("Location not found in favorites")
            } catch (e: Exception) {
                mutableMessage.emit("Error fetching location: ${e.message}")
            }
        }
    }

    fun insertFavLocation(country: String, lat: Double, lon: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val currentWeather = repository.getCurrentWeather(lon, lat, "en", "metric").first()
                val forecast = repository.getWeatherForecast(lat, lon, "en", "metric").first()

                currentWeather.fold(
                    onSuccess = { weather ->
                        forecast.fold(
                            onSuccess = { forecastData ->
                                val location = SavedLocation(
                                    country,
                                    lat,
                                    lon,
                                    weather.toList(),
                                    forecastData.toList()
                                )
                                repository.insertLocation(location)
                                mutableMessage.emit("Location added to favorites")
                            },
                            onFailure = { error ->
                                mutableMessage.emit("Error getting forecast: ${error.message}")
                            }
                        )
                    },
                    onFailure = { error ->
                        mutableMessage.emit("Error getting weather: ${error.message}")
                    }
                )
            } catch (e: Exception) {
                mutableMessage.emit("Error adding location to favorites: ${e.message}")
            }
        }
    }

    fun removeLocation(location: SavedLocation) {
        viewModelScope.launch {
            try {
                repository.deleteLocation(location)
                mutableMessage.emit("Location removed from favorites")
                // Refresh the saved locations list
                getFavLocations()
            } catch (e: Exception) {
                mutableMessage.emit("Error removing location: ${e.message}")
            }
        }
    }

    private fun cacheHomeData(weather: WeatherResponse?, forecast: WeatherForecastResponse?) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val existingHome = homeCacheRepo.getHome()
                val home = existingHome?.copy(
                    weatherPojo = weather?.let { listOf(it) } ?: existingHome.weatherPojo,
                    forecastPojo = forecast?.let { listOf(it) } ?: existingHome.forecastPojo
                ) ?: HomeCached(
                    weatherPojo = weather?.let { listOf(it) } ?: emptyList(),
                    forecastPojo = forecast?.let { listOf(it) } ?: emptyList()
                )
                homeCacheRepo.insertHome(home)
            } catch (e: Exception) {
                Log.e("WeatherViewModel", "Error caching data: ${e.message}")
            }
        }
    }

    // Keep the existing methods for location handling
    fun setLocationMethod(method: String) {
        _locationMethod.value = method
    }

    fun getSavedHomeLocation(context: Context): LatLng {
        val location = _selectedHomeLocation.value
        return if (location != null) {
            LatLng(location.second, location.third)
        } else {
            val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
            val lat = sharedPreferences.getFloat("home_latitude", 0.0f).toDouble()
            val lng = sharedPreferences.getFloat("home_longitude", 0.0f).toDouble()
            LatLng(lat, lng)
        }
    }

    fun updateSelectedHomeLocation(name: String, lat: Double, lng: Double, context: Context) {
        _selectedHomeLocation.value = Triple(name, lat, lng)
        val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        sharedPreferences.edit()
            .putString("home_location_name", name)
            .putFloat("home_latitude", lat.toFloat())
            .putFloat("home_longitude", lng.toFloat())
            .apply()
    }
}

@Composable
@RequiresApi(Build.VERSION_CODES.O)
fun getDayNameFromDate(date: String): String {
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
    val localDate = LocalDate.parse(date, formatter)
    return if (localDate.dayOfWeek == LocalDate.now().dayOfWeek) {
        stringResource(R.string.today)
    }else {
        localDate.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
    }
}


//
//class WeatherViewModel(private val repository: WeatherRepository , private val homeCacheRepo: HomeCacheRepo) : ViewModel() {
//
//
//    private val _weatherState = MutableStateFlow<WeatherResponse?>(null)
//    val weatherState: StateFlow<WeatherResponse?> = _weatherState
//
//    private val _forecastState = MutableStateFlow<WeatherForecastResponse?>(null)
//    val forecastState: StateFlow<WeatherForecastResponse?> = _forecastState
//
//    private val _loading = MutableStateFlow(false)
//    val loading: StateFlow<Boolean> = _loading
//
//    private val _errorMessage = MutableStateFlow<String?>(null)
//    val errorMessage: StateFlow<String?> = _errorMessage
//
//    private val _savedLocations = MutableStateFlow<List<SavedLocation>>(emptyList())
//
//    private val mutableMessage = MutableSharedFlow<String>()
//    val message: SharedFlow<String> = mutableMessage.asSharedFlow()
//
//    private val _locationMethod = MutableStateFlow("GPS")
//    val locationMethod: StateFlow<String> = _locationMethod
//
//
//    private val _selectedHomeLocation = MutableStateFlow<Triple<String, Double, Double>?>(null)
//
//    val isLoading = MutableStateFlow(false)
//
//    fun fetchWeather(long: Double, lat: Double, lang: String, unit: String) {
//        var newTemp = mapTemperatureUnit(unit)
//        var newLang = setLanguage(lang)
//        viewModelScope.launch {
//            _loading.value = true
//            repository.getCurrentWeather(long, lat, newLang, newTemp).collectLatest { result ->
//                result.onSuccess { _weatherState.value = it }
//                result.onFailure { _errorMessage.value = it.message }
//                _loading.value = false
//            }
//        }
//    }
//
//    fun fetchWeatherForecast(lat: Double, lon: Double, lang: String, unit: String) {
//        var newTemp = mapTemperatureUnit(unit)
//        var newLang = setLanguage(lang)
//        viewModelScope.launch {
//            _loading.value = true
//            repository.getWeatherForecast(lat, lon, newTemp, newLang).collectLatest { result ->
//                result.onSuccess {
//                    _forecastState.value = it
//                    Log.d("forecast", "fetchWeatherForecast: ${it.list.get(0).main.temp}")
//                }
//                result.onFailure { _errorMessage.value = it.message }
//                _loading.value = false
//            }
//        }
//    }
//
//    private val _selectedLocation = MutableStateFlow(Triple("", 0.0, 0.0))
//    var selectedLocation = _selectedLocation.asStateFlow()
//
//    val savedLocations: Flow<List<SavedLocation>> = repository.getAllLocations()
//
//    fun updateSelectedLocation(locationName: String, log: Double, lat: Double) {
//        _selectedLocation.value = Triple(locationName, log, lat)
//    }
//
//    fun getFavLocations() {
//        viewModelScope.launch {
//            repository.getAllLocations().collectLatest { locations ->
//                _savedLocations.value = locations.sortedBy { it.name }
//            }
//        }
//    }
//
//
//
//    fun removeLocation(location: SavedLocation) {
//        viewModelScope.launch {
//            repository.deleteLocation(location)
//        }
//    }
//    fun getFavLocation(lat: Double, lon: Double) {
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                val location = repository.getLocationByCoordinates(lat, lon)
//                location?.let {
//                    _selectedLocation.emit(Triple(it.name, it.longitude, it.latitude))
//                } ?: mutableMessage.emit("Location not found in favorites")
//            } catch (e: Exception) {
//                mutableMessage.emit("Error fetching location: ${e.message}")
//            }
//        }
//    }
//    fun insertFavLocation(country: String, lat: Double, lon: Double) {
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                val currentWeather = repository.getCurrentWeather(lon, lat, "en", "metric").first()
//                val forecast = repository.getWeatherForecast(lat, lon, "en", "metric").first()
//                val location = SavedLocation(
//                    country,
//                    lat,
//                    lon,
//                    currentWeather.getOrNull()!!.toList(),
//                    forecast.getOrNull()!!.toList()
//                )
//                repository.insertLocation(location)
//                mutableMessage.emit("Location added to favorites")
//                Log.d("insert", "insertFavLocation: $location")
//            } catch (e: Exception) {
//                mutableMessage.emit("Error adding location to favorites: ${e.message}")
//            }
//        }
//
//    }
//
//
//    suspend fun getHome(): HomeCached {
//        return homeCacheRepo.getHome()
//    }
//
//    fun fetchWeather(long: Double, lat: Double, lang: String, unit: String, context: Context) {
//        val newTemp = mapTemperatureUnit(unit)
//        val newLang = lang
//        Log.d("lang", "fetchWeather: $newLang")
//
//        viewModelScope.launch {
//            _loading.value = true
//            if (isInternetAvailable(context)) {
//                repository.getCurrentWeather(long, lat, newLang, newTemp).collectLatest { result ->
//                    result.onSuccess {
//                        _weatherState.value = it
//                        cacheHomeData(it, null)
//                    }
//                    result.onFailure {
//                        _errorMessage.value = it.message
//                        fetchWeatherFromCache()
//                    }
//                }
//            } else {
//                fetchWeatherFromCache()
//            }
//            _loading.value = false
//        }
//    }
//
//    fun fetchWeatherForecast(
//        lat: Double,
//        lon: Double,
//        lang: String,
//        unit: String,
//        context: Context
//    ) {
//        val newTemp = mapTemperatureUnit(unit)
//        val newLang = lang
//
//        viewModelScope.launch {
//            _loading.value = true
//            if (isInternetAvailable(context)) {
//                repository.getWeatherForecast(lat, lon, newLang, newTemp).collectLatest { result ->
//                    result.onSuccess {
//                        _forecastState.value = it
//                        cacheHomeData(null, it)
//                    }
//                    result.onFailure {
//                        _errorMessage.value = it.message
//                        fetchForecastFromCache()
//                    }
//                }
//            } else {
//                fetchForecastFromCache()
//            }
//            _loading.value = false
//        }
//    }
//
//    private suspend fun fetchWeatherFromCache() {
//        try {
//            val cachedHome = homeCacheRepo.getHome()
//            _weatherState.value = cachedHome?.weatherPojo?.firstOrNull()
//            Log.d("WeatherViewModel", "Loaded weather from cache")
//        } catch (e: Exception) {
//            Log.e("WeatherViewModel", "Error fetching cached weather: ${e.message}")
//        }
//    }
//
//    private suspend fun fetchForecastFromCache() {
//        try {
//            val cachedHome = homeCacheRepo.getHome()
//            _forecastState.value = cachedHome?.forecastPojo?.firstOrNull()
//            Log.d("WeatherViewModel", "Loaded forecast from cache")
//        } catch (e: Exception) {
//            Log.e("WeatherViewModel", "Error fetching cached forecast: ${e.message}")
//        }
//    }
//
//    private fun cacheHomeData(weather: WeatherResponse?, forecast: WeatherForecastResponse?) {
//        viewModelScope.launch(Dispatchers.IO) {
//            try {
//                val existingHome = homeCacheRepo.getHome()
//                val home = existingHome?.copy(
//                    weatherPojo = weather?.let { listOf(it) } ?: existingHome.weatherPojo,
//                    forecastPojo = forecast?.let { listOf(it) } ?: existingHome.forecastPojo
//                ) ?: HomeCached(weatherPojo = weather?.let { listOf(it) } ?: emptyList(),
//                    forecastPojo = forecast?.let { listOf(it) } ?: emptyList())
//                homeCacheRepo.insertHome(home)
//                Log.d("WeatherViewModel", "Data cached successfully")
//            } catch (e: Exception) {
//                Log.e("WeatherViewModel", "Error caching data: ${e.message}")
//            }
//        }
//    }
//
//    fun setLocationMethod(method: String) {
//        _locationMethod.value = method
//    }
//
//    fun getSavedHomeLocation(context: Context): LatLng {
//        val location = _selectedHomeLocation.value
//
//        if (location != null) {
//            return LatLng(location.second, location.third)
//        }
//        // Load from SharedPreferences
//        val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
//        val lat = sharedPreferences.getFloat("home_latitude", 0.0f).toDouble()
//        val lng = sharedPreferences.getFloat("home_longitude", 0.0f).toDouble()
//        Log.d("getSavedHomeLocation", "Loaded from SharedPreferences: lat=$lat, lng=$lng")
//        return LatLng(lat, lng)
//
//
//    }
//
//    fun updateSelectedHomeLocation(name: String, lat: Double, lng: Double, context: Context) {
//        _selectedHomeLocation.value = Triple(name, lat, lng)
//        Log.d("getSavedHomeLocation", "Updated Location: $name, $lat, $lng")
//
//        // Save to SharedPreferences
//        val sharedPreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
//        sharedPreferences.edit()
//            .putString("home_location_name", name)
//            .putFloat("home_latitude", lat.toFloat())
//            .putFloat("home_longitude", lng.toFloat())
//            .apply()
//    }
//
//}
