package com.example.skycast.data.database.FavDataBase

import com.example.skycast.data.models.SavedLocation
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
    suspend fun getLocationByCoordinates(lat: Double, lon: Double): SavedLocation? {
        return locationDao.getLocationByCoordinates(lat, lon)
    }



}