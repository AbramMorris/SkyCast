package com.example.skycast.data.database.AlarmDataBase

import androidx.room.*
import com.example.skycast.data.models.AlarmEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface AlarmDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlarm(alarm: AlarmEntity)

    @Update
    suspend fun updateAlarm(alarm: AlarmEntity)

    @Delete
    suspend fun deleteAlarm(alarm: AlarmEntity)

    @Query("SELECT * FROM alarms ORDER BY hour, minute ASC")
    fun getAllAlarms(): Flow<List<AlarmEntity>>
}