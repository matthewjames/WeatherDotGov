package com.mattjamesdev.weatherdotgov.repository

import android.util.Log
import com.mattjamesdev.weatherdotgov.data.model.forecastarea.toDomain
import com.mattjamesdev.weatherdotgov.data.util.BaseApiResponse
import com.mattjamesdev.weatherdotgov.data.util.NetworkResult
import com.mattjamesdev.weatherdotgov.domain.model.ForecastAreaV2
import com.mattjamesdev.weatherdotgov.domain.StateData
import com.mattjamesdev.weatherdotgov.network.WEATHER_DOT_GOV_BASE_URL
import com.mattjamesdev.weatherdotgov.network.WeatherDotGovAPI
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivityRepository : BaseApiResponse() {
    private val TAG = "SearchActivityRepo"

    private val weatherDotGovService = getWeatherDotGovService() as WeatherDotGovAPI

    suspend fun getForecastAreaV2(
        latitude: Double,
        longitude: Double
    ): Flow<StateData<ForecastAreaV2?>> = flow {
        val networkResult = safeApiCall {
            Log.d(TAG, "fetching Forecast Area Data for lat=$latitude, long=$longitude")
            weatherDotGovService.getForecastAreaV2(latitude, longitude)
        }

        when(networkResult){
            is NetworkResult.Success -> {
                emit(StateData.Ready(data = networkResult.data?.toDomain()))
            }
            is NetworkResult.Error -> {
                emit(StateData.Error(message = networkResult.message.orEmpty()))
            }
            is NetworkResult.Loading -> {
                emit(StateData.Loading())
            }
        }
    }



    suspend fun getForecastArea(latitude: Double, longitude: Double) = weatherDotGovService.getForecastArea(latitude, longitude)

    suspend fun getGridpointData(forecastArea: ForecastAreaV2) = weatherDotGovService.getGridpointData(forecastArea.wfo, forecastArea.gridX, forecastArea.gridY)

    suspend fun getHourlyForecastData(forecastArea: ForecastAreaV2) = weatherDotGovService.getHourlyForecastData(forecastArea.wfo, forecastArea.gridX, forecastArea.gridY)

    suspend fun getSevenDayForecastData(forecastArea: ForecastAreaV2) = weatherDotGovService.get7DayForecastData(forecastArea.wfo, forecastArea.gridX, forecastArea.gridY)

    suspend fun getAlertData(forecastArea: ForecastAreaV2) = weatherDotGovService.getAlertData(forecastArea.zoneId)

    private fun getWeatherDotGovService() : Any {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY

        val client = OkHttpClient.Builder()
            .cache(null)
            .addInterceptor(interceptor)
            .build()

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(WEATHER_DOT_GOV_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(WeatherDotGovAPI::class.java)
    }
}