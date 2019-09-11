package com.ihiabe.josh.weatherapp.data.network

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.ihiabe.josh.weatherapp.data.network.response.CurrentWeatherResponse
import com.ihiabe.josh.weatherapp.internal.NoConnectivityException

class WeatherNetworkDataSourceImpl(private val apiService: ApixuWeatherApiService) : WeatherNetworkDataSource {

    private val _downloadedCurrentWeatherData = MutableLiveData<CurrentWeatherResponse>()
    override val downloadedCurrentWeatherData: LiveData<CurrentWeatherResponse>
        get() = _downloadedCurrentWeatherData

    override suspend fun fetchCurrentWeather(location: String, languageCode: String) {
        try {
            val fetchedCurrentWeather = apiService.getCurrentWeather(location,languageCode).await()
            _downloadedCurrentWeatherData.postValue(fetchedCurrentWeather)
        } catch (e: NoConnectivityException){
            Log.e("connectivity","no internet connection",e)
        }
    }
}