package com.mattjamesdev.weatherdotgov.data.model.alerts


import com.google.gson.annotations.SerializedName

data class Parameters(
    @SerializedName("AWIPSidentifier")
    val aWIPSidentifier: List<String?>?,
    @SerializedName("BLOCKCHANNEL")
    val bLOCKCHANNEL: List<String?>?,
    @SerializedName("eventEndingTime")
    val eventEndingTime: List<String?>?,
    @SerializedName("expiredReferences")
    val expiredReferences: List<String?>?,
    @SerializedName("NWSheadline")
    val nWSheadline: List<String?>?,
    @SerializedName("VTEC")
    val vTEC: List<String?>?,
    @SerializedName("WMOidentifier")
    val wMOidentifier: List<String?>?
)