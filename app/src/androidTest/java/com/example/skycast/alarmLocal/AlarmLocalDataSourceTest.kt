package com.example.skycast.alarmLocal

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.skycast.data.database.AlarmDataBase.AlarmDao
import com.example.skycast.data.database.AlarmDataBase.AlarmLocalDataSource
import com.example.skycast.data.database.FavDataBase.AppDatabase
import com.example.skycast.data.models.AlarmEntity
import com.example.skycast.data.models.Response
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class AlarmLocalDataSourceTest {
    private lateinit var alarmDao: AlarmDao
    private lateinit var localDataSource: AlarmLocalDataSource
    private lateinit var database: AppDatabase

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).allowMainThreadQueries()
            .build()
        alarmDao = database.alarmDao()
        localDataSource = AlarmLocalDataSource(alarmDao)
    }
    @After
    fun teardown(){
        database.close()
    }
    @Test
    fun insertTaskAndGetTaskFromBothDataSourceAndDao() = runTest {

        val alarm = AlarmEntity(1, 2, 50, "alaaarm", 22.555, 33.6677)

        localDataSource.insertAlarm(alarm)

        val result = localDataSource.getAllAlarms().first()
        assertThat(result, `is`(Response.Success(alarm)))

        assertThat(result.isNotEmpty(), `is`(true))
        assertThat(result[0], `is`(alarm))
        assertThat(result[0].latitude, `is`(22.555))
    }
    @Test
    fun insertAlert_DeletethisItem_checkisTheListHaveTheAlertOrNot() = runBlocking {

        val alarm = AlarmEntity(1, 2, 50, "alaaarm", 22.555, 33.6677)

        localDataSource.insertAlarm(alarm)
        localDataSource.deleteAlarm(alarm)

        val result = localDataSource.getAllAlarms().first()

        assertThat(result.isEmpty(), `is`(true))
    }

}