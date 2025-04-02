package com.example.skycast.data.repo

import com.example.skycast.data.database.AlarmDataBase.AlarmLocalDataSource
import com.example.skycast.data.mapper.toAlarmList
import com.example.skycast.data.models.AlarmEntity
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before

import org.junit.Test

//class AlarmRepoImpTest {
//    private lateinit var localDataSource : AlarmLocalDataSource
//    private lateinit var repo : AlarmRepoImp
//    @Before
//    fun setUp() {
//        localDataSource = mockk(relaxed = true)
//        repo = AlarmRepoImp(localDataSource)
//    }
//    @Test
//    fun GetAlarm()= runTest {
//        val alarms = repo.getAllAlarms().toAlarmList()
//        assertThat(alarms.isEmpty(), `is`(true))
//    }
//    @Test
//    fun deleteAlarmAndGetTest()= runTest {
//        val alarm = AlarmEntity(1,12,22,"alarmm",0.0,0.0)
//        repo.insertAlarm(alarm)
//        repo.deleteAlarm(alarm)
//        val alarms = repo.getAllAlarms().toAlarmList()
//        assertThat(alarms.isEmpty(), `is`(true))
//    }
//}

class AlarmRepoImpTest {

    private lateinit var alarmRepo: AlarmRepoImp
    private val localDataSource: AlarmLocalDataSource = mockk() // Mock the data source

    @Before
    fun setup() {
        // Set the Main dispatcher for coroutines
        Dispatchers.setMain(Dispatchers.Unconfined)
        alarmRepo = AlarmRepoImp(localDataSource)
    }
    @Test
    fun `test insertAlarm calls localDataSource insertAlarm`() = runTest {
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
    fun `test deleteAlarm calls localDataSource deleteAlarm`() = runTest {
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
        // Reset the Main dispatcher after each test
        Dispatchers.resetMain()
    }
}