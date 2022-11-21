package com.mattjamesdev.weatherdotgov.data.model.sevenday


import com.google.gson.annotations.SerializedName

data class SevenDayForecastResponse(
    @SerializedName("geometry")
    val geometry: Geometry?,
    @SerializedName("properties")
    val properties: Properties?,
    @SerializedName("type")
    val type: String?
)