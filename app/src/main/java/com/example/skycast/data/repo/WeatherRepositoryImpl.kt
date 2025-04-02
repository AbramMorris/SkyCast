package com.example.skycast.data.repo
import com.example.skycast.data.database.FavDataBase.LocalDataSource
import com.example.skycast.data.models.SavedLocation
import com.example.skycast.data.models.WeatherForecastResponse
import com.example.skycast.data.models.WeatherResponse
import com.example.skycast.data.remotes.WeatherRemoteDataSourcee
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow



class WeatherRepositoryImpl(private val remoteDataSource: WeatherRemoteDataSourcee, private val localDataSource: LocalDataSource) :
    WeatherRepository {

    override fun getCurrentWeather( long :Double, lat :Double, lang: String,unit: String): Flow<Result<WeatherResponse>> = flow {
        emit(Result.success(remoteDataSource.getCurrentWeather(long, lat,lang ,unit).body()!!))
    }.catch { e ->
        emit(Result.failure(e))
    }
    override fun getWeatherForecast(lat: Double, lon: Double, lang: String,unit: String): Flow<Result<WeatherForecastResponse>> = flow {
        emit(Result.success(remoteDataSource.getWeatherForecast(lat, lon, lang ,unit).body()!!))
    }.catch { e ->
        emit(Result.failure(e))
    }
    override fun getAllLocations(): Flow<List<SavedLocation>> {
        return localDataSource.getAllLocations()
    }

    override suspend fun insertLocation(location: SavedLocation) {
        localDataSource.insertLocation(location)
    }

    override suspend fun deleteLocation(location: SavedLocation) {
        localDataSource.deleteLocation(location)
    }
    override suspend fun getLocationByCoordinates(lat: Double, lon: Double): SavedLocation? {
        return localDataSource.getLocationByCoordinates(lat, lon)
    }

}