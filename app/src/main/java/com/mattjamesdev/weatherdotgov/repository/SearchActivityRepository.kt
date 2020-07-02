package com.mattjamesdev.weatherdotgov.repository

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.mattjamesdev.weatherdotgov.network.BASE_URL
import com.mattjamesdev.weatherdotgov.network.WeatherDotGovNetwork
import com.mattjamesdev.weatherdotgov.network.model.DayForecast
import com.mattjamesdev.weatherdotgov.network.model.ForecastData
import com.mattjamesdev.weatherdotgov.network.model.Location
import com.mattjamesdev.weatherdotgov.network.model.Period
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
    val sevenDayForecastData = MutableLiveData<MutableList<DayForecast>>()

    private val client = OkHttpClient.Builder().build()
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    private val service = retrofit.create(WeatherDotGovNetwork::class.java)

    fun changeState(){
        isLoading.value = !(isLoading.value != null && isLoading.value!!)
    }

    fun getLocationProperties(coords : String){
        isLoading.value = true

        service.getProperties(coords).enqueue(object: Callback<Location>{
            override fun onResponse(call: Call<Location>, response: Response<Location>) {
                if(response.isSuccessful){
                    val result = response.body()!!
                    Log.d(TAG, result.toString())

                    val wfo = result.properties.gridId
                    val x = result.properties.gridX
                    val y = result.properties.gridY

                    getHourlyForecastData(wfo, x, y)
                    getSevenDayForecastData(wfo, x, y)
                }
            }

            override fun onFailure(call: Call<Location>, t: Throwable) {
                isLoading.value = false
                Toast.makeText(application, "Failed to retrieve location properties", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun getHourlyForecastData(wfo: String, x: Int, y: Int){
        service.getHourlyForecastData(wfo, x, y).enqueue(object: Callback<ForecastData>{
            override fun onResponse(call: Call<ForecastData>, response: Response<ForecastData>) {
                isLoading.value = false
                hourlyForecastData.value = response.body()!!
                Log.d(TAG, "Hourly: ${hourlyForecastData.value.toString()}")
            }

            override fun onFailure(call: Call<ForecastData>, t: Throwable) {
                isLoading.value = false
            }
        })
    }

    private fun getSevenDayForecastData(wfo: String, x: Int, y: Int){
        service.get7DayForecastData(wfo, x, y).enqueue(object: Callback<ForecastData>{
            override fun onResponse(call: Call<ForecastData>, response: Response<ForecastData>) {
                isLoading.value = false

                parseSevenDayForecastData(response.body()!!)

//                sevenDayForecastData.value = response.body()!!
                Log.d(TAG, "Seven day: ${sevenDayForecastData.value.toString()}")
            }

            override fun onFailure(call: Call<ForecastData>, t: Throwable) {
                isLoading.value = false
            }
        })
    }

    // parses List<Period> in ForecastData to List<DayForecast>
    private fun parseSevenDayForecastData(data: ForecastData){
        val dayForecasts = mutableListOf<DayForecast>()
        val date = data.properties.periods[0].startTime
        var periods = mutableListOf<Period>(data.properties.periods[0])
        var dayForecast = DayForecast(date, periods)

        for(i in 1 until (data.properties.periods.size)){
            val currentPeriod = data.properties.periods[i]

            if(currentPeriod.startTime.substring(0..9) == dayForecast.date.substring(0..9)){
                // add current to dayForecast
                dayForecast.periods.add(currentPeriod)
            } else {
                // add dayForecast to list, create new dayForecast object with current
                dayForecasts.add(dayForecast)
                periods = mutableListOf<Period>(currentPeriod)
                dayForecast = DayForecast(currentPeriod.startTime, periods)
            }
        }

        dayForecasts.add(dayForecast) // add last element to list

        sevenDayForecastData.value = dayForecasts
    }
}