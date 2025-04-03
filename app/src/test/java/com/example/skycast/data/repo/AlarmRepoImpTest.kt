package com.example.skycast.data.repo

import com.example.skycast.data.database.AlarmDataBase.AlarmLocalDataSource
import com.example.skycast.data.models.AlarmEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
class AlarmRepoImpTest {

    private lateinit var alarmRepo: AlarmRepoImp
    private val localDataSource: AlarmLocalDataSource = mockk()

    @Before
    fun setup() {
        alarmRepo = AlarmRepoImp(localDataSource)
    }
    @Test
    fun test_insertAlarm_calls_localDataSource_insertAlarm_() = runTest {

        val alarm = AlarmEntity(1, 12, 22, "alarm", 0.0, 0.0)

        coEvery { localDataSource.insertAlarm(any()) } returns Unit

        alarmRepo.insertAlarm(alarm)

        coVerify { localDataSource.insertAlarm(alarm) }
    }
    @Test
    fun test_deleteAlarm_calls_localDataSource_deleteAlarm() = runTest {

        val alarm = AlarmEntity(1, 12, 22, "alarm", 0.0, 0.0)

        coEvery { localDataSource.deleteAlarm(any()) } returns Unit

        alarmRepo.deleteAlarm(alarm)

        coVerify { localDataSource.deleteAlarm(alarm) }
    }
}