package com.ihiabe.josh.weatherapp

import android.app.Application
import androidx.preference.PreferenceManager
import com.ihiabe.josh.weatherapp.data.db.ForecastDatabase
import com.ihiabe.josh.weatherapp.data.network.*
import com.ihiabe.josh.weatherapp.data.provider.UnitProvider
import com.ihiabe.josh.weatherapp.data.provider.UnitProviderImpl
import com.ihiabe.josh.weatherapp.data.repository.ForecastRepository
import com.ihiabe.josh.weatherapp.data.repository.ForecastRepositoryImpl
import com.ihiabe.josh.weatherapp.ui.weather.current.CurrentWeatherViewModelFactory
import com.jakewharton.threetenabp.AndroidThreeTen
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

class ForecastApplication: Application(), KodeinAware {
    override val kodein = Kodein.lazy {
        import(androidXModule(this@ForecastApplication))

        bind() from singleton { ForecastDatabase(instance()) }
        bind() from singleton { instance<ForecastDatabase>().currentWeatherDao() }
        bind<ConnectivityInterceptor>() with singleton { ConnectivityInterceptorImpl(instance()) }
        bind() from singleton { ApixuWeatherApiService(instance()) }
        bind<WeatherNetworkDataSource>() with singleton { WeatherNetworkDataSourceImpl(instance()) }
        bind<ForecastRepository>() with singleton { ForecastRepositoryImpl(instance(),instance()) }
        bind<UnitProvider>() with singleton { UnitProviderImpl(instance()) }
        bind() from provider { CurrentWeatherViewModelFactory(instance(),instance()) }

    }

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        PreferenceManager.setDefaultValues(this,R.xml.preferences,false)
    }
}