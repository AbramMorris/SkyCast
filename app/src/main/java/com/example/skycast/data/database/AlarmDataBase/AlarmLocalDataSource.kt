package com.example.skycast.data.database.AlarmDataBase

import com.example.skycast.data.models.AlarmEntity
import kotlinx.coroutines.flow.Flow


class AlarmLocalDataSource(private val alarmDao: AlarmDao) {
    suspend fun insertAlarm(alarm: AlarmEntity) {
        alarmDao.insertAlarm(alarm)
    }
    suspend fun updateAlarm(alarm: AlarmEntity) {
        alarmDao.updateAlarm(alarm)
    }
    suspend fun deleteAlarm(alarm: AlarmEntity) {
        alarmDao.deleteAlarm(alarm)
    }
    fun getAllAlarms(): Flow<List<AlarmEntity>> {
        return alarmDao.getAllAlarms()
    }

}