package com.ihiabe.josh.weatherapp.data.network

import androidx.lifecycle.LiveData
import com.ihiabe.josh.weatherapp.data.network.response.CurrentWeatherResponse

interface WeatherNetworkDataSource {
    val downloadedCurrentWeatherData: LiveData<CurrentWeatherResponse>

    suspend fun fetchCurrentWeather(location: String, languageCode: String)
}