package com.ihiabe.josh.weatherapp.data.repository

import androidx.lifecycle.LiveData
import com.ihiabe.josh.weatherapp.data.db.unitLocalized.UnitSpecificCurrentWeatherEntry

interface ForecastRepository {
    suspend fun getCurrentWeather(metric: Boolean): LiveData<out UnitSpecificCurrentWeatherEntry>
}