package com.example.skycast.alarmLocal

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.skycast.data.database.AlarmDataBase.AlarmDao
import com.example.skycast.data.database.FavDataBase.AppDatabase
import com.example.skycast.data.models.AlarmEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@SmallTest
class AlarmDaoTest {
    private lateinit var database: AppDatabase
    private lateinit var alarmDao: AlarmDao
    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            AppDatabase::class.java
        ).build()
        alarmDao = database.alarmDao()
    }
    @After
    fun teardown(){
        database.close()
    }
    @Test
    fun insertAlarmAndGetAlarmFromDao() = runTest {
        // Arrange: Create an alarm entity
        val alarm = AlarmEntity(1, 2, 50, "alaaarm", 22.555, 33.6677)

        // Act: Insert the alarm
        alarmDao.insertAlarm(alarm)

        // Collect the flow result
        val result = alarmDao.getAllAlarms().first() // Collect the first emitted value

        // Assert: Check if the inserted alarm exists
        MatcherAssert.assertThat(result.isNotEmpty(), `is`(true)) // Ensure list is not empty
        MatcherAssert.assertThat(result[0], `is`(alarm)) // Ensure the alarm matches the expected one
        MatcherAssert.assertThat(result[0].latitude, `is`(22.555)) // Validate latitude
    }
    @Test
    fun insertAlarm_DeletethisAlarm_checkisTheListHaveTheAlertOrNot() = runBlocking {
        // Arrange: Create an alarm entity
        val alarm = AlarmEntity(1, 2, 50, "alaaarm", 22.555, 33.6677)

        // Act: Insert and delete the alarm
        alarmDao.insertAlarm(alarm)
        alarmDao.deleteAlarm(alarm)

        // Collect the flow and check the result
        val result = alarmDao.getAllAlarms().first()

        // Assert: The list should be empty, not null
        MatcherAssert.assertThat(result.isEmpty(), `is`(true))
    }
}