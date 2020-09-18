package com.mattjamesdev.weatherdotgov.repository

import androidx.lifecycle.MutableLiveData
import com.mattjamesdev.weatherdotgov.network.BASE_URL
import com.mattjamesdev.weatherdotgov.network.WeatherDotGovAPI
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivityRepository {
    private val TAG = "SearchActivityRepo"
    private val isLoading = MutableLiveData<Boolean>()

    init {
        isLoading.value = false
    }

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(OkHttpClient.Builder().cache(null).build())
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service = retrofit.create(WeatherDotGovAPI::class.java)

    suspend fun getLocationProperties(coords: String) = service.getProperties(coords)

    suspend fun getHourlyForecastData(wfo: String, x: Int, y: Int) = service.getHourlyForecastData(wfo, x, y)

    suspend fun getSevenDayForecastData(wfo: String, x: Int, y: Int) = service.get7DayForecastData(wfo, x, y)

}