package com.ihiabe.josh.weatherapp.data.provider

import com.ihiabe.josh.weatherapp.internal.UnitSystem

interface UnitProvider {
    fun getUnitSystem(): UnitSystem
}