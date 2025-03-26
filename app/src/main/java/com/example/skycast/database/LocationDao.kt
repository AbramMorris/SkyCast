package com.example.skycast.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

@Dao
interface LocationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: SavedLocation)

    @Query("SELECT * FROM saved_locations")
    fun getAllLocations(): Flow<List<SavedLocation>>

    @Delete
    suspend fun deleteLocation(location: SavedLocation)
}
