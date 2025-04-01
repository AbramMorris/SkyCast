package com.example.skycast.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.skycast.data.repo.HomeCacheRepo
import com.example.skycast.data.repo.WeatherRepository

class WeatherViewModelFactory(private val repository: WeatherRepository, private val homeCacheRepo: HomeCacheRepo) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WeatherViewModel(repository, homeCacheRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}