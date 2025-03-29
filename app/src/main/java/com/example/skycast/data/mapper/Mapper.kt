package com.example.skycast.data.mapper

import com.example.skycast.data.models.Response
import com.example.skycast.data.models.WeatherForecastResponse
import com.example.skycast.data.models.WeatherResponse

fun WeatherResponse.toList(): List<WeatherResponse> {
    return listOf(this)
}
fun WeatherForecastResponse.toList(): List<WeatherForecastResponse> {
    return listOf(this)
}

fun Result<WeatherResponse>.toResponse(): Response<WeatherResponse> {
    return Response.Success(this.getOrNull()!!)
}