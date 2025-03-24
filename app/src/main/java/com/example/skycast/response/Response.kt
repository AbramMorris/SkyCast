package com.example.skycast.response

import com.example.skycast.models.WeatherResponse

sealed class Response {
    data object Loading : Response()
    data class Success(val data: List<WeatherResponse>) : Response()
    data class Failure(val error: Throwable) : Response()
}