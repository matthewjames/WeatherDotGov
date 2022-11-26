package com.mattjamesdev.weatherdotgov.network

import com.mattjamesdev.weatherdotgov.data.model.alerts.AlertsResponse
import com.mattjamesdev.weatherdotgov.data.model.forecastarea.ForecastAreaResponse
import com.mattjamesdev.weatherdotgov.data.model.gridpoint.GridpointResponse
import com.mattjamesdev.weatherdotgov.data.model.hourly.HourlyForecastResponse
import com.mattjamesdev.weatherdotgov.data.model.sevenday.SevenDayForecastResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

const val WEATHER_DOT_GOV_BASE_URL = "https://api.weather.gov"

interface WeatherDotGovAPI {
    @GET("points/{latitude},{longitude}")
    suspend fun getForecastArea(
        @Path("latitude") latitude: Double,
        @Path("longitude") longitude: Double
    ): ForecastAreaResponse

    @GET("points/{latitude},{longitude}")
    suspend fun getForecastAreaV2(
        @Path("latitude") latitude: Double,
        @Path("longitude") longitude: Double
    ): Response<ForecastAreaResponse>

    @GET("gridpoints/{wfo}/{x},{y}")
    suspend fun getGridpointData(
        @Path("wfo") wfo: String,
        @Path("x") x: Int,
        @Path("y") y: Int
    ): GridpointResponse

    @GET("gridpoints/{wfo}/{x},{y}/forecast/hourly")
    suspend fun getHourlyForecastData(
        @Path("wfo") wfo: String,
        @Path("x") x: Int,
        @Path("y") y: Int
    ): HourlyForecastResponse

    @GET("gridpoints/{wfo}/{x},{y}/forecast")
    suspend fun get7DayForecastData(
        @Path("wfo") wfo: String,
        @Path("x") x: Int,
        @Path("y") y: Int
    ): SevenDayForecastResponse

    @GET("alerts/active/zone/{zoneId}")
    suspend fun getAlertData(@Path("zoneId") zoneId: String): AlertsResponse


}