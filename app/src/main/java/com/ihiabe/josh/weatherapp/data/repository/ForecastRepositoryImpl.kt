package com.ihiabe.josh.weatherapp.data.repository

import androidx.lifecycle.LiveData
import com.ihiabe.josh.weatherapp.data.db.CurrentWeatherDao
import com.ihiabe.josh.weatherapp.data.db.WeatherLocationDao
import com.ihiabe.josh.weatherapp.data.db.entity.WeatherLocation
import com.ihiabe.josh.weatherapp.data.db.unitLocalized.UnitSpecificCurrentWeatherEntry
import com.ihiabe.josh.weatherapp.data.network.WeatherNetworkDataSource
import com.ihiabe.josh.weatherapp.data.network.response.CurrentWeatherResponse
import com.ihiabe.josh.weatherapp.data.provider.LocationProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.ZonedDateTime
import java.util.*

class ForecastRepositoryImpl(private val currentWeatherDao: CurrentWeatherDao,
                             private val weatherLocationDao: WeatherLocationDao,
                             private val locationProvider: LocationProvider,
                             private val weatherNetworkDataSource: WeatherNetworkDataSource) : ForecastRepository {
    init {
        weatherNetworkDataSource.downloadedCurrentWeatherData.observeForever { newCurrentWeather->
            persistFetchedCurrentWeather(newCurrentWeather)
        }
    }
    override suspend fun getCurrentWeather(metric: Boolean): LiveData<out UnitSpecificCurrentWeatherEntry> {
        return withContext(Dispatchers.IO){
            initWeatherData()
            return@withContext if (metric) currentWeatherDao.getWeatherMetric()
            else currentWeatherDao.getWeatherImperial()
        }
    }

    override suspend fun getWeatherLocation(): LiveData<WeatherLocation> {
        return withContext(Dispatchers.IO){
            return@withContext weatherLocationDao.getLocation()
        }
    }

    private fun persistFetchedCurrentWeather(fetchedWeather: CurrentWeatherResponse){
        GlobalScope.launch(Dispatchers.IO) {
            currentWeatherDao.upsert(fetchedWeather.currentWeatherEntry)
            weatherLocationDao.upsert(fetchedWeather.location)
        }
    }

    private suspend fun initWeatherData(){
        val lastWeatherLocation = weatherLocationDao.getLocation().value

        if (lastWeatherLocation == null || locationProvider.hasLocationChanged(lastWeatherLocation)){
            fetchCurrentWeather()
            return
        }

        if (isFetchCurrentNeeded(lastWeatherLocation.zonedDateTime))
            fetchCurrentWeather()
    }

    private suspend fun fetchCurrentWeather(){
        weatherNetworkDataSource.fetchCurrentWeather(locationProvider.getPreferredLocationString(),Locale.getDefault().language)
    }

    private fun isFetchCurrentNeeded(lastFetchTime: ZonedDateTime): Boolean{
        val thirtyMinutesAgo = ZonedDateTime.now().minusMinutes(30)
        return lastFetchTime.isBefore(thirtyMinutesAgo)
    }
}