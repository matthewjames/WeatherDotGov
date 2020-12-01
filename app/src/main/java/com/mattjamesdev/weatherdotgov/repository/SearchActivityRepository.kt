package com.mattjamesdev.weatherdotgov.repository

import android.util.Log
import com.mattjamesdev.weatherdotgov.model.AlertData
import com.mattjamesdev.weatherdotgov.model.ForecastArea
import com.mattjamesdev.weatherdotgov.model.ForecastData
import com.mattjamesdev.weatherdotgov.model.GridpointData
import com.mattjamesdev.weatherdotgov.network.WEATHER_DOT_GOV_BASE_URL
import com.mattjamesdev.weatherdotgov.network.WeatherDotGovAPI
import kotlinx.coroutines.delay
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivityRepository {
    private val TAG = "SearchActivityRepo"

    private val weatherDotGovService = getWeatherDotGovService() as WeatherDotGovAPI

    suspend fun getForecastArea(latitude: Double, longitude: Double) : Response<ForecastArea> = weatherDotGovService.getForecastArea(latitude, longitude)

    suspend fun getGridpointData(wfo: String, x: Int, y: Int): GridpointData {
        delay(500)
        Log.d(TAG, "Returning gridpoint data...")
        return weatherDotGovService.getGridpointData(wfo, x, y)
    }

    suspend fun getHourlyForecastData(wfo: String, x: Int, y: Int): ForecastData {
        delay(500)
        Log.d(TAG, "Returning hourly forecast data...")
        return weatherDotGovService.getHourlyForecastData(wfo, x, y)
    }

    suspend fun getSevenDayForecastData(wfo: String, x: Int, y: Int): ForecastData {
        delay(500)
        Log.d(TAG, "Returning seven day forecast data...")
        return weatherDotGovService.get7DayForecastData(wfo, x, y)
    }

    suspend fun getAlertData(zoneId: String): AlertData {
        delay(500)
        Log.d(TAG, "Returning alert data...")
        return weatherDotGovService.getAlertData(zoneId)
    }

    private fun getWeatherDotGovService() : Any {
        val client = OkHttpClient.Builder().cache(null).build()

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(WEATHER_DOT_GOV_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(WeatherDotGovAPI::class.java)
    }
}