package com.mattjamesdev.weatherdotgov.data.model.alerts


import com.google.gson.annotations.SerializedName

data class Feature(
    @SerializedName("geometry")
    val geometry: Any?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("properties")
    val properties: Properties?,
    @SerializedName("type")
    val type: String?
)