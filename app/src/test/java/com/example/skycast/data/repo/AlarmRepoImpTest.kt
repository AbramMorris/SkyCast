package com.example.skycast.data.repo

import com.example.skycast.data.database.AlarmDataBase.AlarmLocalDataSource
import com.example.skycast.data.models.AlarmEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
class AlarmRepoImpTest {

    private lateinit var alarmRepo: AlarmRepoImp
    private val localDataSource: AlarmLocalDataSource = mockk()

    @Before
    fun setup() {
        Dispatchers.setMain(Dispatchers.Unconfined)
        alarmRepo = AlarmRepoImp(localDataSource)
    }
    @Test
    fun test_insertAlarm_calls_localDataSource_insertAlarm_() = runTest {
        // Arrange
        val alarm = AlarmEntity(1, 12, 22, "alarm", 0.0, 0.0)
        // Mock the insertAlarm function so it does nothing
        coEvery { localDataSource.insertAlarm(any()) } returns Unit
        // Act
        alarmRepo.insertAlarm(alarm)
        // Assert
        coVerify { localDataSource.insertAlarm(alarm) } // Verify that insertAlarm was called
    }
    @Test
    fun test_deleteAlarm_calls_localDataSource_deleteAlarm() = runTest {
        // Arrange
        val alarm = AlarmEntity(1, 12, 22, "alarm", 0.0, 0.0)
        // Mock the deleteAlarm function so it does nothing
        coEvery { localDataSource.deleteAlarm(any()) } returns Unit
        // Act
        alarmRepo.deleteAlarm(alarm)
        // Assert
        coVerify { localDataSource.deleteAlarm(alarm) } // Verify that deleteAlarm was called
    }
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
}