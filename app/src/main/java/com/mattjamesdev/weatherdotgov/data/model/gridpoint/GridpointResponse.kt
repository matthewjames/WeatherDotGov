package com.mattjamesdev.weatherdotgov.data.model.gridpoint


import com.google.gson.annotations.SerializedName

data class GridpointResponse(
    @SerializedName("geometry")
    val geometry: Geometry?,
    @SerializedName("id")
    val id: String?,
)