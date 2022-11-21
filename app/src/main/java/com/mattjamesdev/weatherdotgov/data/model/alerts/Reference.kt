package com.mattjamesdev.weatherdotgov.data.model.alerts


import com.google.gson.annotations.SerializedName

data class Reference(
    @SerializedName("@id")
    val id: String?,
    @SerializedName("identifier")
    val identifier: String?,
    @SerializedName("sender")
    val sender: String?,
    @SerializedName("sent")
    val sent: String?
)