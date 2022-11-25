package com.mattjamesdev.weatherdotgov.data.model.hourly


import com.google.gson.annotations.SerializedName

data class HourlyForecastResponse(
    @SerializedName("properties")
    val properties: Properties?
){
    companion object {
        val NO_DATA = HourlyForecastResponse(
            properties = null
        )
    }
}
