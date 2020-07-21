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

class SearchActivityRepository(val application: Application) {
    val TAG = "SearchActivityRepo"
    val isLoading = MutableLiveData<Boolean>()
    val hourlyForecastData = MutableLiveData<ForecastData>()
    val sevenDayForecastData = MutableLiveData<ForecastData>()
    val forecastDataBundle = MutableLiveData<ForecastDataBundle>()

//    private val client = OkHttpClient.Builder().build()
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service = retrofit.create(WeatherDotGovNetwork::class.java)

    fun changeState(){
        isLoading.value = !(isLoading.value != null && isLoading.value!!)
    }

    suspend fun getLocationProperties(coords: String) = service.getProperties(coords)

    suspend fun getHourlyForecastData(wfo: String, x: Int, y: Int) = service.getHourlyForecastData(wfo, x, y)

    suspend fun getSevenDayForecastData(wfo: String, x: Int, y: Int) = service.get7DayForecastData(wfo, x, y)

//    suspend fun getLocationProperties(coords : String){
//        isLoading.value = true
//
//        GlobalScope.launch(Dispatchers.IO){
//            service.getProperties(coords).enqueue(object : Callback<Location> {
//                override fun onResponse(call: Call<Location>, response: Response<Location>) {
//                    if (response.isSuccessful) {
//                        val result = response.body()!!
//                        Log.d(TAG, result.toString())
//
//                        val wfo = result.properties.gridId
//                        val x = result.properties.gridX
//                        val y = result.properties.gridY
//
//                        GlobalScope.launch(Dispatchers.IO) {
//                            getHourlyForecastData(wfo, x, y)
//                            getSevenDayForecastData(wfo, x, y)
//                        }
//                    }
//                }
//
//                override fun onFailure(call: Call<Location>, t: Throwable) {
//                    GlobalScope.launch(Dispatchers.Main){isLoading.value = false}
//                    Toast.makeText(
//                        application,
//                        "Failed to retrieve location properties",
//                        Toast.LENGTH_LONG
//                    ).show()
//                }
//            })
//        }
//    }
//
//    private suspend fun getHourlyForecastData(wfo: String, x: Int, y: Int){
//        service.getHourlyForecastData(wfo, x, y).enqueue(object: Callback<ForecastData>{
//            override fun onResponse(call: Call<ForecastData>, response: Response<ForecastData>) {
//                GlobalScope.launch(Dispatchers.Main){
//                    isLoading.value = false
//                    hourlyForecastData.value = response.body()!!
//                }
//                Log.d(TAG, "Hourly: ${hourlyForecastData.value.toString()}")
//            }
//
//            override fun onFailure(call: Call<ForecastData>, t: Throwable) {
//                GlobalScope.launch(Dispatchers.Main){isLoading.value = false}
//            }
//        })
//    }
//
//    private suspend fun getSevenDayForecastData(wfo: String, x: Int, y: Int){
//        service.get7DayForecastData(wfo, x, y).enqueue(object: Callback<ForecastData>{
//            override fun onResponse(call: Call<ForecastData>, response: Response<ForecastData>) {
//                GlobalScope.launch(Dispatchers.Main){
//                    isLoading.value = false
//                    sevenDayForecastData.value = response.body()!!
//                }
//                Log.d(TAG, "Seven day: ${sevenDayForecastData.value.toString()}")
//            }
//
//            override fun onFailure(call: Call<ForecastData>, t: Throwable) {
//                GlobalScope.launch(Dispatchers.Main){isLoading.value = false}
//            }
//        })
//    }
}