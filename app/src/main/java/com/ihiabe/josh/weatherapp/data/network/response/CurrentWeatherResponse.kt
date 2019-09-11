package com.ihiabe.josh.weatherapp.data.network.response

import com.google.gson.annotations.SerializedName
import com.ihiabe.josh.weatherapp.data.db.entity.CurrentWeatherEntry
import com.ihiabe.josh.weatherapp.data.db.entity.WeatherLocation

data class CurrentWeatherResponse(
    @SerializedName("current")
    val currentWeatherEntry: CurrentWeatherEntry,
    val location: WeatherLocation
)