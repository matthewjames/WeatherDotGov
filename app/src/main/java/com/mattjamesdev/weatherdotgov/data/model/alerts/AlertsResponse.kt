package com.mattjamesdev.weatherdotgov.data.model.alerts


import com.google.gson.annotations.SerializedName

data class AlertsResponse(
    @SerializedName("features")
    val features: List<Feature?>?,
    @SerializedName("title")
    val title: String?,
    @SerializedName("type")
    val type: String?,
    @SerializedName("updated")
    val updated: String?
)