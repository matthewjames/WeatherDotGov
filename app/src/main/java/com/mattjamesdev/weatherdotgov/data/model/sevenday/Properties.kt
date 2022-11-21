package com.mattjamesdev.weatherdotgov.data.model.sevenday


import com.google.gson.annotations.SerializedName
import com.mattjamesdev.weatherdotgov.data.model.Period

data class Properties(
    @SerializedName("elevation")
    val elevation: Elevation?,
    @SerializedName("forecastGenerator")
    val forecastGenerator: String?,
    @SerializedName("generatedAt")
    val generatedAt: String?,
    @SerializedName("periods")
    val periods: List<Period?>?,
    @SerializedName("units")
    val units: String?,
    @SerializedName("updateTime")
    val updateTime: String?,
    @SerializedName("updated")
    val updated: String?,
    @SerializedName("validTimes")
    val validTimes: String?
)