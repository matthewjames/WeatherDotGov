package com.mattjamesdev.weatherdotgov.data.model.forecastarea


import com.google.gson.annotations.SerializedName

data class Properties(
    @SerializedName("county")
    val county: String?,
    @SerializedName("cwa")
    val cwa: String?,
    @SerializedName("fireWeatherZone")
    val fireWeatherZone: String?,
    @SerializedName("forecast")
    val forecast: String?,
    @SerializedName("forecastGridData")
    val forecastGridData: String?,
    @SerializedName("forecastHourly")
    val forecastHourly: String?,
    @SerializedName("forecastOffice")
    val forecastOffice: String?,
    @SerializedName("forecastZone")
    val forecastZone: String?,
    @SerializedName("gridId")
    val gridId: String?,
    @SerializedName("gridX")
    val gridX: Int?,
    @SerializedName("gridY")
    val gridY: Int?,
    @SerializedName("@id")
    val id: String?,
    @SerializedName("observationStations")
    val observationStations: String?,
    @SerializedName("radarStation")
    val radarStation: String?,
    @SerializedName("relativeLocation")
    val relativeLocation: RelativeLocation?,
    @SerializedName("timeZone")
    val timeZone: String?,
    @SerializedName("@type")
    val type: String?
)