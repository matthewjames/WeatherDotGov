package com.mattjamesdev.weatherdotgov.network

import com.mattjamesdev.weatherdotgov.network.model.ForecastData
import com.mattjamesdev.weatherdotgov.network.model.Location
import retrofit2.http.GET
import retrofit2.http.Path

const val BASE_URL = "https://api.weather.gov"

interface WeatherDotGovAPI {
    @GET("points/{coordinates}")
    suspend fun getProperties(@Path("coordinates") key: String) : Location

    @GET("gridpoints/{wfo}/{x},{y}/forecast/hourly")
    suspend fun getHourlyForecastData(@Path("wfo") wfo: String, @Path("x") x: Int, @Path("y") y: Int) : ForecastData

    @GET("gridpoints/{wfo}/{x},{y}/forecast")
    suspend fun get7DayForecastData(@Path("wfo") wfo: String, @Path("x") x: Int, @Path("y") y: Int) : ForecastData
}