package com.example.skycast.alertTestCases

import com.example.skycast.data.models.AlarmEntity
import com.example.skycast.data.repo.AlarmRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AlarmFakeRepo : AlarmRepository {

    val alarmList = mutableListOf<AlarmEntity>()

    override suspend fun insertAlarm(alarm: AlarmEntity) {
        alarmList.add(alarm)
    }

    override fun getAllAlarms(): Flow<List<AlarmEntity>> {
        val alarmflow = flow {
            val updatedAlarmList = alarmList.toList()
            emit(updatedAlarmList)
        }
        return alarmflow
    }

    override suspend fun deleteAlarm(alarm: AlarmEntity) {
        alarmList.remove(alarm)
    }

    override suspend fun updateAlarm(alarm: AlarmEntity) {
        TODO("Not yet implemented")
    }
}