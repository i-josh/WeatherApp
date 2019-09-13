package com.ihiabe.josh.weatherapp.data.provider

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.ihiabe.josh.weatherapp.data.db.entity.WeatherLocation
import com.ihiabe.josh.weatherapp.internal.LocationPermissionNotGrantedException
import com.ihiabe.josh.weatherapp.internal.asDeferredAsync
import kotlinx.coroutines.Deferred
import kotlin.math.abs

const val USE_DEVICE_LOCATION = "USE_DEVICE_LOCATION"
const val CUSTOM_LOCATION =  "CUSTOM_LOCATION"

class LocationProviderImpl(
    private val fusedLocationProviderClient: FusedLocationProviderClient,
    context: Context) : PreferenceProvider(context), LocationProvider {
    private val appContext = context.applicationContext

    override suspend fun hasLocationChanged(lastWeatherLocation: WeatherLocation): Boolean {
        val deviceLocationChanged = try {
            hasDeviceLocationChanged(lastWeatherLocation)
        } catch (e: LocationPermissionNotGrantedException){
            return false
        }
        return deviceLocationChanged || hasCustomLocationChanged(lastWeatherLocation)
    }

    override suspend fun getPreferredLocationString(): String {
        if (isUsingDeviceLocation()){
            try {
                val deviceLocation = getLastDeviceLocationAsync().await()
                    ?: return "${getCustomLocationName()}"
                return "${deviceLocation.latitude},${deviceLocation.longitude}"
            }catch (e: LocationPermissionNotGrantedException){
                    return "${getCustomLocationName()}"
            }
        } else{
            return "${getCustomLocationName()}"
        }
    }

    private suspend fun hasDeviceLocationChanged(lastWeatherLocation: WeatherLocation): Boolean {
        if (!isUsingDeviceLocation())
            return false

        val deviceLocation = getLastDeviceLocationAsync().await() ?: return false

        //comparing doubles cannot be done with "=="
        val comparisonThreshold = 0.03
        return abs(deviceLocation.latitude - lastWeatherLocation.lat) > comparisonThreshold &&
                abs(deviceLocation.longitude - lastWeatherLocation.lon) > comparisonThreshold

    }

    private fun hasCustomLocationChanged(lastWeatherLocation: WeatherLocation): Boolean{
        val customLocationName = getCustomLocationName()
        return customLocationName != lastWeatherLocation.name
    }

    private fun isUsingDeviceLocation(): Boolean{
        return preferences.getBoolean(USE_DEVICE_LOCATION,true)
    }

    private fun getCustomLocationName(): String?{
        return preferences.getString(CUSTOM_LOCATION,null)
    }

    @SuppressLint("MissingPermission")
    private fun getLastDeviceLocationAsync(): Deferred<Location?>{
        return if (hasLocationPermission())
            fusedLocationProviderClient.lastLocation.asDeferredAsync()
        else
            throw LocationPermissionNotGrantedException()
    }

    private fun hasLocationPermission(): Boolean{
        return ContextCompat.checkSelfPermission(appContext,Manifest.permission.ACCESS_COARSE_LOCATION)==
                PackageManager.PERMISSION_GRANTED
    }
}