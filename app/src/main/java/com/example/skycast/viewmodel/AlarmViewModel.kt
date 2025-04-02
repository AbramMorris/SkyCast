package com.example.skycast.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.skycast.data.models.AlarmEntity
import com.example.skycast.data.repo.AlarmRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class AlarmViewModel(private val AlarmRepository: AlarmRepository) : ViewModel() {


    val savedAlarms: Flow<List<AlarmEntity>> = AlarmRepository.getAllAlarms()
    var _selectedAlarmLocation = MutableStateFlow(Triple("",0.0,0.0))
    var selectedAlarmLocation = _selectedAlarmLocation.asStateFlow()

    fun insertAlarm(alarm: AlarmEntity) {
        viewModelScope.launch {
            AlarmRepository.insertAlarm(alarm)
        }
    }

     fun getAllAlarms(): Flow<List<AlarmEntity>> {
        return AlarmRepository.getAllAlarms()
    }

    fun deleteAlarm(alarm: AlarmEntity) {
        viewModelScope.launch {
            AlarmRepository.deleteAlarm(alarm)
        }
    }
    fun updateAlarm(alarm: AlarmEntity) {
        viewModelScope.launch {
            AlarmRepository.updateAlarm(alarm)
        }
    }

//    fun updateSelectedAlarmLocation(name: String, lat: Double, lon: Double) {
//        _selectedAlarmLocation.value = Triple(name, lat, lon)
//        sendLocationToWeatherViewModel() // Send location to WeatherViewModel
//    }
//
//    fun sendLocationToWeatherViewModel() {
//        val (locationName, latitude, longitude) = _selectedAlarmLocation.value
//        weatherViewModel.updateSelectedLocation(locationName, longitude, latitude)
//    }
}

