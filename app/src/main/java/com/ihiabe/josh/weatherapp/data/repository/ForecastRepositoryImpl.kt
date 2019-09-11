package com.ihiabe.josh.weatherapp.data.repository

import androidx.lifecycle.LiveData
import com.ihiabe.josh.weatherapp.data.db.CurrentWeatherDao
import com.ihiabe.josh.weatherapp.data.db.unitLocalized.UnitSpecificCurrentWeatherEntry
import com.ihiabe.josh.weatherapp.data.network.WeatherNetworkDataSource
import com.ihiabe.josh.weatherapp.data.network.response.CurrentWeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.ZonedDateTime
import java.util.*

class ForecastRepositoryImpl(private val currentWeatherDao: CurrentWeatherDao,
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

    private fun persistFetchedCurrentWeather(fetchedWeather: CurrentWeatherResponse){
        GlobalScope.launch(Dispatchers.IO) {
            currentWeatherDao.upsert(fetchedWeather.currentWeatherEntry)
        }
    }

    private suspend fun initWeatherData(){
        if (isFetchCurrentNeeded(ZonedDateTime.now().minusHours(1)))
            fetchCurrentWeather()
    }

    private suspend fun fetchCurrentWeather(){
        weatherNetworkDataSource.fetchCurrentWeather("madrid",Locale.getDefault().language)
    }

    private fun isFetchCurrentNeeded(lastFetchTime: ZonedDateTime): Boolean{
        val thirtyMinutesAgo = ZonedDateTime.now().minusMinutes(30)
        return lastFetchTime.isBefore(thirtyMinutesAgo)
    }
}