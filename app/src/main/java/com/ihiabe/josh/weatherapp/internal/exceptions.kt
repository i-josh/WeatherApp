package com.ihiabe.josh.weatherapp.internal

import java.io.IOException
import java.lang.Exception

class NoConnectivityException: IOException()
class LocationPermissionNotGrantedException : Exception()