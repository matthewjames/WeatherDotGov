package com.mattjamesdev.weatherdotgov.data.model.alerts


import com.google.gson.annotations.SerializedName

data class Geocode(
    @SerializedName("SAME")
    val sAME: List<String?>?,
    @SerializedName("UGC")
    val uGC: List<String?>?
)