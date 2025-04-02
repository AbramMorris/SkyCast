package com.example.skycast.data.mapper

import com.example.skycast.data.models.AlarmEntity
import com.example.skycast.data.models.Response
import com.example.skycast.data.models.WeatherForecastResponse
import com.example.skycast.data.models.WeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull

fun WeatherResponse.toList(): List<WeatherResponse> {
    return listOf(this)
}
fun WeatherForecastResponse.toList(): List<WeatherForecastResponse> {
    return listOf(this)
}

fun Result<WeatherResponse>.toResponse(): Response<WeatherResponse> {
    return Response.Success(this.getOrNull()!!)
}
//fun Response<List<AlarmEntity>>.toAlarmList(): List<AlarmEntity> {
//    return when (this) {
//        is Response.Success -> this.data
//        is Response.Failure -> emptyList()
//        is Response.Loading -> emptyList()
//    }
//}
suspend fun Flow<List<AlarmEntity>>.toAlarmList(): List<AlarmEntity> {
    return this.firstOrNull() ?: emptyList()
}
