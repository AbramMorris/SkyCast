package com.example.skycast.data.repo

import com.example.skycast.data.database.HomeDataBase.HomeLocalDataSource
import com.example.skycast.data.models.HomeCached
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before

import org.junit.Test

class HomeCacheRepoTest {
    private var localDataSource = mockk<HomeLocalDataSource>()
    private var repo = HomeCacheRepo(localDataSource)
    @Before
    fun setUp() {
        localDataSource = mockk(relaxed = true)
        repo = HomeCacheRepo(localDataSource)
    }
    @Test
    fun insertHome()= runTest {
        val home = HomeCached(1,mockk(),mockk())
        repo.insertHome(home)
        val homeCached = repo.getHome()
        assertEquals(home,homeCached)
    }

    @Test
    fun getHome() {
    }

    @Test
    fun deleteHome() {
    }
}