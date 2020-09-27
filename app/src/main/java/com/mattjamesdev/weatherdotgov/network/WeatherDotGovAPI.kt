package com.mattjamesdev.weatherdotgov.network

import com.mattjamesdev.weatherdotgov.network.model.ForecastData
import com.mattjamesdev.weatherdotgov.network.model.ForecastArea
import retrofit2.http.GET
import retrofit2.http.Path

const val WEATHER_DOT_GOV_BASE_URL = "https://api.weather.gov"

interface WeatherDotGovAPI {
    @GET("points/{latitude},{longitude}")
    suspend fun getForecastArea(@Path("latitude")latitude: Double, @Path("longitude") longitude: Double) : ForecastArea

    @GET("gridpoints/{wfo}/{x},{y}/forecast/hourly")
    suspend fun getHourlyForecastData(@Path("wfo") wfo: String, @Path("x") x: Int, @Path("y") y: Int) : ForecastData

    @GET("gridpoints/{wfo}/{x},{y}/forecast")
    suspend fun get7DayForecastData(@Path("wfo") wfo: String, @Path("x") x: Int, @Path("y") y: Int) : ForecastData
}