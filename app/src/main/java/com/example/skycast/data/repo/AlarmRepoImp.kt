package com.example.skycast.data.repo


import com.example.skycast.data.database.AlarmDataBase.AlarmLocalDataSource
import com.example.skycast.data.models.AlarmEntity
import kotlinx.coroutines.flow.Flow

class AlarmRepoImp( private val localDataSource: AlarmLocalDataSource) :AlarmRepository {
    override suspend fun insertAlarm(alarm: AlarmEntity) {
        localDataSource.insertAlarm(alarm)
    }
    override fun getAllAlarms(): Flow<List<AlarmEntity>> {
        return localDataSource.getAllAlarms()
    }
    override suspend fun deleteAlarm(alarm: AlarmEntity) {
        localDataSource.deleteAlarm(alarm)
    }

    override suspend fun updateAlarm(alarm: AlarmEntity) {
        localDataSource.updateAlarm(alarm)
    }
}
