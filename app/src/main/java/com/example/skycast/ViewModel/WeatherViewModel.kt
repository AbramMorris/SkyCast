package com.example.skycast.ViewModel
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.skycast.Model.WeatherForecastResponse
import com.example.skycast.Model.WeatherResponse
import com.example.skycast.Remote.WeatherApiServes
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response

class WeatherViewModel(private val weatherApi: WeatherApiServes) : ViewModel() {

    // 🌡️ Current Weather State
    private val _weatherState = MutableStateFlow<WeatherResponse?>(null)
    val weatherState: StateFlow<WeatherResponse?> = _weatherState

    // 🌦️ Weather Forecast State
    private val _forecastState = MutableStateFlow<WeatherForecastResponse?>(null)
    val forecastState: StateFlow<WeatherForecastResponse?> = _forecastState

    // ⏳ Loading State
    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    // ⚠️ Error Message State
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    // 🔹 Fetch Current Weather by City
    fun fetchWeather(city: String, apiKey: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response: Response<WeatherResponse> = weatherApi.getWeather(city, apiKey)
                if (response.isSuccessful) {
                    _weatherState.value = response.body()
                } else {
                    _errorMessage.value = "Error: ${response.message()}"
                    Log.e("WeatherViewModel", "Error: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Exception: ${e.message}"
                Log.e("WeatherViewModel", "Exception: ${e.message}")
            } finally {
                _loading.value = false
            }
        }
    }
    fun fetchWeatherForecast(lat: Double, lon: Double, apiKey: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response: Response<WeatherForecastResponse> = weatherApi.getWeatherForecast(lat, lon, apiKey)
                if (response.isSuccessful) {
                    _forecastState.value = response.body()
                } else {
                    _errorMessage.value = "Error: ${response.message()}"
                    Log.e("WeatherForecastVM", "Error: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                _errorMessage.value = "Exception: ${e.message}"
                Log.e("WeatherForecastVM", "Exception: ${e.message}")
            } finally {
                _loading.value = false
            }
        }
    }
}

class WeatherViewModelFactory(private val weatherApi: WeatherApiServes) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WeatherViewModel(weatherApi) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

