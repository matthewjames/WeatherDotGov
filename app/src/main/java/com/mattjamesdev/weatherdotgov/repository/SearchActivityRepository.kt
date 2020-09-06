package com.mattjamesdev.weatherdotgov.repository

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.mattjamesdev.weatherdotgov.network.BASE_URL
import com.mattjamesdev.weatherdotgov.network.WeatherDotGovNetwork
import com.mattjamesdev.weatherdotgov.network.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivityRepository {
    val TAG = "SearchActivityRepo"
    val isLoading = MutableLiveData<Boolean>()

    init {
        isLoading.value = false
    }

    private val client = OkHttpClient.Builder().cache(null).build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service = retrofit.create(WeatherDotGovNetwork::class.java)

    fun changeState(){
        isLoading.value = !(isLoading.value != null && isLoading.value!!)
    }

    suspend fun getLocationProperties(coords: String) = service.getProperties(coords)

    suspend fun getHourlyForecastData(wfo: String, x: Int, y: Int) = service.getHourlyForecastData(wfo, x, y)

    suspend fun getSevenDayForecastData(wfo: String, x: Int, y: Int) = service.get7DayForecastData(wfo, x, y)

}