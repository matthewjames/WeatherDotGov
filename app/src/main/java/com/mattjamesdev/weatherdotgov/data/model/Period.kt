package com.mattjamesdev.weatherdotgov.data.model


import com.google.gson.annotations.SerializedName

data class Period(
    @SerializedName("detailedForecast")
    val detailedForecast: String?,
    @SerializedName("endTime")
    val endTime: String?,
    @SerializedName("icon")
    val icon: String?,
    @SerializedName("isDaytime")
    val isDaytime: Boolean?,
    @SerializedName("name")
    var name: String?,
    @SerializedName("number")
    val number: Int?,
    @SerializedName("shortForecast")
    val shortForecast: String?,
    @SerializedName("startTime")
    val startTime: String?,
    @SerializedName("temperature")
    val temperature: Int?,
    @SerializedName("temperatureTrend")
    val temperatureTrend: Any?,
    @SerializedName("temperatureUnit")
    val temperatureUnit: String?,
    @SerializedName("windDirection")
    val windDirection: String?,
    @SerializedName("windSpeed")
    val windSpeed: String?
)