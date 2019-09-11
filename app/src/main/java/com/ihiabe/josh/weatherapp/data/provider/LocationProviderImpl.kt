package com.ihiabe.josh.weatherapp.data.provider

import com.ihiabe.josh.weatherapp.data.db.entity.WeatherLocation

class LocationProviderImpl : LocationProvider {
    override suspend fun hasLocationChanged(lastWeatherLocation: WeatherLocation): Boolean {
        return true
    }

    override suspend fun getPreferredLocationString(): String {
        return "Abuja"
    }
}