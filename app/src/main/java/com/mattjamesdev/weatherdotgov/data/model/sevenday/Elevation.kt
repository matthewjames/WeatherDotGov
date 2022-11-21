package com.mattjamesdev.weatherdotgov.data.model.sevenday


import com.google.gson.annotations.SerializedName

data class Elevation(
    @SerializedName("unitCode")
    val unitCode: String?,
    @SerializedName("value")
    val value: Double?
)