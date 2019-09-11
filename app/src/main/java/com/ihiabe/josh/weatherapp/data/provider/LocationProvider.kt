package com.ihiabe.josh.weatherapp.data.provider

import com.ihiabe.josh.weatherapp.data.db.entity.WeatherLocation

interface LocationProvider {
    suspend fun hasLocationChanged(lastWeatherLocation: WeatherLocation) : Boolean
    suspend fun getPreferredLocationString() : String
}