package com.mattjamesdev.weatherdotgov.network

import com.mattjamesdev.weatherdotgov.network.model.ForecastData
import com.mattjamesdev.weatherdotgov.network.model.Location
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

const val BASE_URL = "https://api.weather.gov/"

interface WeatherDotGovNetwork {
    @GET("points/{coordinates}")
    fun getProperties(@Path("coordinates") key: String) : Call<Location>

    @GET("gridpoints/{wfo}/{x},{y}/forecast/hourly")
    fun getHourlyForecastData(@Path("wfo") wfo: String, @Path("x") x: Int, @Path("y") y: Int) : Call<ForecastData>

    @GET("gridpoints/{wfo}/{x},{y}/forecast")
    fun get7DayForecastData(@Path("wfo") wfo: String, @Path("x") x: Int, @Path("y") y: Int) : Call<ForecastData>
}