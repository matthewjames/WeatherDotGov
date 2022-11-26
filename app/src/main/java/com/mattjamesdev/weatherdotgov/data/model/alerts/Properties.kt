package com.mattjamesdev.weatherdotgov.data.model.alerts


import com.google.gson.annotations.SerializedName

data class Properties(
    @SerializedName("affectedZones")
    val affectedZones: List<String?>?,
    @SerializedName("areaDesc")
    val areaDesc: String?,
    @SerializedName("category")
    val category: String?,
    @SerializedName("certainty")
    val certainty: String?,
    @SerializedName("description")
    val description: String?,
    @SerializedName("effective")
    val effective: String?,
    @SerializedName("ends")
    val ends: String?,
    @SerializedName("event")
    val event: String?,
    @SerializedName("expires")
    val expires: String?,
    @SerializedName("geocode")
    val geocode: Geocode?,
    @SerializedName("headline")
    val headline: String?,
    @SerializedName("id")
    val id: String?,
    @SerializedName("instruction")
    val instruction: String?,
    @SerializedName("messageType")
    val messageType: String?,
    @SerializedName("onset")
    val onset: String?,
    @SerializedName("parameters")
    val parameters: Parameters?,
    @SerializedName("references")
    val references: List<Reference?>?,
    @SerializedName("response")
    val response: String?,
    @SerializedName("sender")
    val sender: String?,
    @SerializedName("senderName")
    val senderName: String?,
    @SerializedName("sent")
    val sent: String?,
    @SerializedName("severity")
    val severity: String?,
    @SerializedName("status")
    val status: String?,
    @SerializedName("@type")
    val type: String?,
    @SerializedName("urgency")
    val urgency: String?
)