package com.mattjamesdev.weatherdotgov.data.model.forecastarea


import com.google.gson.annotations.SerializedName

data class RelativeLocation(
    @SerializedName("geometry")
    val geometry: Geometry?,
    @SerializedName("properties")
    val properties: PropertiesX?,
    @SerializedName("type")
    val type: String?
)