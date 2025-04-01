package com.example.skycast.alertTestCases

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.skycast.data.models.AlarmEntity
import com.example.skycast.getOrAwaitValue
import com.example.skycast.viewmodel.AlarmViewModel
import kotlinx.coroutines.test.runTest
import org.hamcrest.CoreMatchers.not
import org.hamcrest.CoreMatchers.nullValue
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AlarmViewModelTest  {
    @get:Rule
    var instanceExecuteRule = InstantTaskExecutorRule()

    lateinit var viewModel: AlarmViewModel
    lateinit var fakeRepo : AlarmFakeRepo
    lateinit var alarm1 : AlarmEntity
    lateinit var alarm2 : AlarmEntity

    @Before
    fun setup(){
        fakeRepo = AlarmFakeRepo()
        viewModel = AlarmViewModel(fakeRepo)
        alarm1 = AlarmEntity(1,2,50,"alaaarm",22.555,33.6677)
        alarm2 = AlarmEntity(2,3,0,"aaaa",33.666,22.333)

    }
    @Test
    fun insertAlert_AndCheckTheListisNotEmpty()= runTest {
        // when->call insert Method
        viewModel.insertAlarm(alarm1)
        // then -> check the result
        var value = viewModel._selectedAlarmLocation.getOrAwaitValue {  }
        assertThat(value, not(nullValue()))

    }

}