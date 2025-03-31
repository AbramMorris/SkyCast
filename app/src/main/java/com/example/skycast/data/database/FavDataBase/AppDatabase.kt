package com.example.skycast.data.database.FavDataBase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.skycast.data.database.AlarmDataBase.AlarmDao
import com.example.skycast.data.database.HomeDataBase.HomeDao
import com.example.skycast.data.models.AlarmEntity
import com.example.skycast.data.models.Converters
import com.example.skycast.data.models.HomeCached
import com.example.skycast.data.models.SavedLocation

@Database(entities = [SavedLocation::class, AlarmEntity::class , HomeCached::class], version = 4, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun locationDao(): LocationDao
    abstract fun alarmDao(): AlarmDao
    abstract fun homeDao(): HomeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"  // Unified database name
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
