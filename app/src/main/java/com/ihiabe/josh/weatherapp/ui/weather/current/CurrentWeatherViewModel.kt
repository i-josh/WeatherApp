package com.ihiabe.josh.weatherapp.ui.weather.current

import androidx.lifecycle.ViewModel
import com.ihiabe.josh.weatherapp.data.provider.UnitProvider
import com.ihiabe.josh.weatherapp.data.repository.ForecastRepository
import com.ihiabe.josh.weatherapp.internal.UnitSystem
import com.ihiabe.josh.weatherapp.internal.lazyDeferred

class CurrentWeatherViewModel(private val forecastRepository: ForecastRepository,
                              unitProvider: UnitProvider) : ViewModel() {

    private val unitSystem = unitProvider.getUnitSystem() //get from settings later
    val isMetric: Boolean
        get() = unitSystem == UnitSystem.METRIC

    val weather by lazyDeferred {
        forecastRepository.getCurrentWeather(isMetric)
    }

    val weatherLocation by lazyDeferred{
        forecastRepository.getWeatherLocation()
    }
}
