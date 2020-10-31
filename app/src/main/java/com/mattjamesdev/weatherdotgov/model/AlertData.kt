package com.mattjamesdev.weatherdotgov.model

import com.google.gson.annotations.SerializedName

data class AlertData(
    val features: List<Feature>,
    val type: String
)

data class Feature(
    val geometry: Any,
    val id: String,
    @SerializedName("properties")
    val alertProperties: AlertProperties,
    val type: String
)

data class AlertProperties(
    val effective: String,
    val ends: String,
    val event: String,
    val headline: String,
    val description: String,
    val instruction: String,
    val affectedZones: List<String>,
    val areaDesc: String,
    val category: String,
    val certainty: String,
    val expires: String,
    val id: String,
    val messageType: String,
    val onset: String,
    val response: String,
    val sender: String,
    val senderName: String,
    val sent: String,
    val severity: String,
    val status: String,
    val urgency: String
)