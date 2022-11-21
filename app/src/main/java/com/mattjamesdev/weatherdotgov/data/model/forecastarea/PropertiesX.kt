package com.mattjamesdev.weatherdotgov.data.model.forecastarea


import com.google.gson.annotations.SerializedName

data class PropertiesX(
    @SerializedName("bearing")
    val bearing: Bearing?,
    @SerializedName("city")
    val city: String?,
    @SerializedName("distance")
    val distance: Distance?,
    @SerializedName("state")
    val state: String?
)