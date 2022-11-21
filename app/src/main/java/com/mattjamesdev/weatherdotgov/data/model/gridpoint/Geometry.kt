package com.mattjamesdev.weatherdotgov.data.model.gridpoint


import com.google.gson.annotations.SerializedName

data class Geometry(
    @SerializedName("coordinates")
    val coordinates: List<List<List<Double?>?>?>?,
    @SerializedName("type")
    val type: String?
)