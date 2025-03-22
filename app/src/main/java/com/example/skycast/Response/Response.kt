package com.example.skycast.Response

import com.example.skycast.Model.WeatherResponse

sealed class Response {
    data object Loading : Response()
    data class Success(val data: List<WeatherResponse>) : Response()
    data class Failure(val error: Throwable) : Response()
}