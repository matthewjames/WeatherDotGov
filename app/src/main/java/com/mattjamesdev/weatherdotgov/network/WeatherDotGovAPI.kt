package com.mattjamesdev.weatherdotgov.network

import com.mattjamesdev.weatherdotgov.model.AlertData
import com.mattjamesdev.weatherdotgov.model.ForecastData
import com.mattjamesdev.weatherdotgov.model.ForecastArea
import com.mattjamesdev.weatherdotgov.model.GridpointData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

const val WEATHER_DOT_GOV_BASE_URL = "https://api.weather.gov"

interface WeatherDotGovAPI {
    @GET("/points/{latitude},{longitude}")
    suspend fun getForecastArea(@Path("latitude")latitude: Double, @Path("longitude") longitude: Double) : Response<ForecastArea>

    @GET("/gridpoints/{wfo}/{x},{y}")
    suspend fun getGridpointData(@Path("wfo") wfo: String, @Path("x") x: Int, @Path("y") y: Int) : GridpointData

    @GET("/gridpoints/{wfo}/{x},{y}/forecast/hourly")
    suspend fun getHourlyForecastData(@Path("wfo") wfo: String, @Path("x") x: Int, @Path("y") y: Int) : ForecastData

    @GET("/gridpoints/{wfo}/{x},{y}/forecast")
    suspend fun get7DayForecastData(@Path("wfo") wfo: String, @Path("x") x: Int, @Path("y") y: Int) : ForecastData

    @GET("/alerts/active/zone/{zoneId}")
    suspend fun getAlertData(@Path("zoneId") zoneId: String) : AlertData


}