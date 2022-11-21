package com.mattjamesdev.weatherdotgov.data.model.forecastarea


import com.google.gson.annotations.SerializedName

data class Distance(
    @SerializedName("unitCode")
    val unitCode: String?,
    @SerializedName("value")
    val value: Double?
)