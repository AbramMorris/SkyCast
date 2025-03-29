package com.example.skycast.viewmodel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.skycast.data.models.AlarmEntity
import com.example.skycast.data.models.SavedLocation
import com.example.skycast.data.repo.AlarmRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class AlarmViewModel(private val AlarmRepository: AlarmRepository) : ViewModel() {


    val savedAlarms: Flow<List<AlarmEntity>> = AlarmRepository.getAllAlarms()
    private var _selectedAlarmLocation = MutableStateFlow(Triple("",0.0,0.0))
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
}
class AlertViewModelFactory(private val repository: AlarmRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlarmViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AlarmViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}