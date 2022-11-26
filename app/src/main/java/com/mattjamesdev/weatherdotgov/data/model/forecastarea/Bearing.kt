package com.mattjamesdev.weatherdotgov.data.model.forecastarea


import com.google.gson.annotations.SerializedName

data class Bearing(
    @SerializedName("unitCode")
    val unitCode: String?,
    @SerializedName("value")
    val value: Int?
)