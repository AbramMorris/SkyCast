package com.example.skycast.data.database.HomeDataBase

import com.example.skycast.data.models.HomeCached

class HomeLocalDataSource ( private val homeDao: HomeDao) {
    suspend fun insertHome(home: HomeCached) {
        homeDao.insertHome(home)
    }
    suspend fun getHome(): HomeCached {
        return homeDao.getHome()
    }
    suspend fun deleteHome() {
        homeDao.deleteHome()
    }
    suspend fun updateHome(home: HomeCached) {
        homeDao.updateHome(home)
    }
}