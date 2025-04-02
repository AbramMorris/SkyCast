package com.example.skycast.data.repo

import com.example.skycast.data.models.AlarmEntity
import com.example.skycast.data.models.Response
import kotlinx.coroutines.flow.Flow

interface AlarmRepository{
    suspend fun insertAlarm(alarm: AlarmEntity)
    fun getAllAlarms(): Flow<List<AlarmEntity>>
    suspend fun deleteAlarm(alarm: AlarmEntity)
    suspend fun updateAlarm(alarm: AlarmEntity)
}