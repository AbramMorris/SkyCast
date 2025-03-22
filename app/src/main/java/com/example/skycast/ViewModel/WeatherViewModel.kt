package com.example.skycast.ViewModel
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.skycast.Model.WeatherForecastResponse
import com.example.skycast.Model.WeatherResponse
import com.example.skycast.Reposatory.WeatherRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

class WeatherViewModel(private val repository: WeatherRepository) : ViewModel() {

    private val _weatherState = MutableStateFlow<WeatherResponse?>(null)
    val weatherState: StateFlow<WeatherResponse?> = _weatherState

    private val _forecastState = MutableStateFlow<WeatherForecastResponse?>(null)
    val forecastState: StateFlow<WeatherForecastResponse?> = _forecastState

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun fetchWeather( long :Double, lat :Double) {
        viewModelScope.launch {
            _loading.value = true
            repository.getCurrentWeather( long , lat ).collectLatest { result ->
                result.onSuccess { _weatherState.value = it }
                result.onFailure { _errorMessage.value = it.message }
                _loading.value = false
            }
        }
    }

    fun fetchWeatherForecast(lat: Double, lon: Double, apiKey: String) {
        viewModelScope.launch {
            _loading.value = true
            repository.getWeatherForecast(lat, lon, apiKey).collectLatest { result ->
                result.onSuccess { _forecastState.value = it }
                result.onFailure { _errorMessage.value = it.message }
                _loading.value = false
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
    return localDate.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
}