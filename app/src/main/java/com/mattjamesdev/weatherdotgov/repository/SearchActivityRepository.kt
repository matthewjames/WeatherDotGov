package com.mattjamesdev.weatherdotgov.repository

import com.mattjamesdev.weatherdotgov.network.WEATHER_DOT_GOV_BASE_URL
import com.mattjamesdev.weatherdotgov.network.WeatherDotGovAPI
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivityRepository {
    private val TAG = "SearchActivityRepo"

    private val weatherDotGovService = getWeatherDotGovService() as WeatherDotGovAPI

    suspend fun getForecastArea(latitude: Double, longitude: Double) = weatherDotGovService.getForecastArea(latitude, longitude)

    suspend fun getGridpointData(wfo: String, x: Int, y: Int) = weatherDotGovService.getGridpointData(wfo, x, y)

    suspend fun getHourlyForecastData(wfo: String, x: Int, y: Int) = weatherDotGovService.getHourlyForecastData(wfo, x, y)

    suspend fun getSevenDayForecastData(wfo: String, x: Int, y: Int) = weatherDotGovService.get7DayForecastData(wfo, x, y)

    suspend fun getAlertData(zoneId: String) = weatherDotGovService.getAlertData(zoneId)

    private fun getWeatherDotGovService() : Any {
        val client = OkHttpClient.Builder()
            .cache(null)
            .build()

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(WEATHER_DOT_GOV_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(WeatherDotGovAPI::class.java)
    }
}