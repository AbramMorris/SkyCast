package com.example.skycast.data.repo

import com.example.skycast.data.database.HomeDataBase.HomeLocalDataSource
import com.example.skycast.data.models.HomeCached

class HomeCacheRepo( private val homeLocalDataSource: HomeLocalDataSource) {
    suspend fun insertHome(home: HomeCached) {
        homeLocalDataSource.insertHome(home)
    }
    suspend fun getHome(): HomeCached {
        return homeLocalDataSource.getHome()
    }
    suspend fun deleteHome() {
        homeLocalDataSource.deleteHome()
    }
    suspend fun updateHome(home: HomeCached) {
        homeLocalDataSource.updateHome(home)
    }


}