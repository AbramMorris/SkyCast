package com.example.skycast.database

import kotlinx.coroutines.flow.Flow

class LocalDataSource(private val locationDao: LocationDao) {

    fun getAllLocations(): Flow<List<SavedLocation>> {
        return locationDao.getAllLocations()
    }

    suspend fun insertLocation(location: SavedLocation) {
        locationDao.insertLocation(location)
    }

    suspend fun deleteLocation(location: SavedLocation) {
        locationDao.deleteLocation(location)
    }
}