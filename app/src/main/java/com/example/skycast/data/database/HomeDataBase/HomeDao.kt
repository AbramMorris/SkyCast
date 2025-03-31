package com.example.skycast.data.database.HomeDataBase

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.skycast.data.models.HomeCached
@Dao
interface HomeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHome(home: HomeCached)

    @Query("SELECT * FROM home_cached")
    suspend fun getHome(): HomeCached

    @Query("DELETE FROM home_cached")
    suspend fun deleteHome()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateHome(home: HomeCached)

}