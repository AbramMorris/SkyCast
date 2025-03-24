package com.example.skycast.response


sealed class LocationResponse<out T> {
    data object Loading : LocationResponse<Nothing>()
    data class Success<T>(val data: T) : LocationResponse<T>()
    data class Failure(val error: Throwable) : LocationResponse<Nothing>()
}