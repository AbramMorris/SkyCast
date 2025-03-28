package com.example.skycast.viewmodel
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.skycast.data.models.SavedLocation
import com.example.skycast.data.models.WeatherForecastResponse
import com.example.skycast.data.models.WeatherResponse
import com.example.skycast.data.repo.WeatherRepository
import com.example.skycast.data.models.Response
import com.example.skycast.util.mapTemperatureUnit
import com.example.skycast.util.setLanguage
import com.example.skycast.data.mapper.toList
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

class WeatherViewModel(private val repository: WeatherRepository) : ViewModel() {
//    private val mutableFavCity = MutableStateFlow<Response>(Response.Loading)
//    val cityName: StateFlow<Response> = mutableFavCity.asStateFlow()


    private val _weatherState = MutableStateFlow<WeatherResponse?>(null)
    val weatherState: StateFlow<WeatherResponse?> = _weatherState

    private val _forecastState = MutableStateFlow<WeatherForecastResponse?>(null)
    val forecastState: StateFlow<WeatherForecastResponse?> = _forecastState

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _savedLocations = MutableStateFlow<List<SavedLocation>>(emptyList())

    private val mutableMessage = MutableSharedFlow<String>()
    val message: SharedFlow<String> = mutableMessage.asSharedFlow()



    fun fetchWeather( long :Double, lat :Double, lang: String ,unit :String)  {
        var newTemp =mapTemperatureUnit(unit)
        var newLang = setLanguage(lang)
        viewModelScope.launch {
            _loading.value = true
            repository.getCurrentWeather( long , lat , newLang ,newTemp).collectLatest { result ->
                result.onSuccess { _weatherState.value = it }
                result.onFailure { _errorMessage.value = it.message }
                _loading.value = false
            }
        }
    }

    fun fetchWeatherForecast(lat: Double, lon: Double, lang: String ,unit: String) {
        var newTemp =mapTemperatureUnit(unit)
        var newLang = setLanguage(lang)
        viewModelScope.launch {
            _loading.value = true
            repository.getWeatherForecast(lat, lon, newLang ,newTemp).collectLatest { result ->
                result.onSuccess { _forecastState.value = it }
                result.onFailure { _errorMessage.value = it.message }
                _loading.value = false
            }
        }
    }

    private val _selectedLocation = MutableStateFlow(Triple("",0.0,0.0))
    var selectedLocation = _selectedLocation.asStateFlow()

    val savedLocations: Flow<List<SavedLocation>> = repository.getAllLocations()

    fun updateSelectedLocation( locationName: String, log : Double, lat :Double) {
        _selectedLocation.value = Triple(locationName,log,lat)
    }

    fun getFavLocations() {
        viewModelScope.launch {
            repository.getAllLocations().collectLatest { locations ->
                _savedLocations.value = locations.sortedBy { it.name }
            }
        }
    }

    fun removeLocation(location: SavedLocation) {
        viewModelScope.launch {
            repository.deleteLocation(location)
        }
    }

    fun insertFavLocation(country: String,lat: Double, lon: Double){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val currentWeather = repository.getCurrentWeather(lon, lat, "en", "metric").first()
                val forecast = repository.getWeatherForecast(lat, lon, "en", "metric").first()
                val location = SavedLocation(
                    country,
                    lat,
                    lon,
                    currentWeather.getOrNull()!!.toList(),
                    forecast.getOrNull()!!.toList()
                )
                repository.insertLocation(location)
                mutableMessage.emit("Location added to favorites")
                Log.d("insert", "insertFavLocation: $location")
            } catch (e: Exception) {
                mutableMessage.emit("Error adding location to favorites: ${e.message}")
            }
        }

    }


}


class WeatherViewModelFactory(private val repository: WeatherRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WeatherViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

@RequiresApi(Build.VERSION_CODES.O)
fun getDayNameFromDate(date: String): String {

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
    val localDate = LocalDate.parse(date, formatter)
    return if (localDate.dayOfWeek == LocalDate.now().dayOfWeek) {
         "Today"
    }else {
        localDate.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
    }
}
