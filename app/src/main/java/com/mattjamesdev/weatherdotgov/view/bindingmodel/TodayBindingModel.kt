package com.mattjamesdev.weatherdotgov.view.bindingmodel

data class TodayBindingModel(
    val dateTime: String,
    val todayHigh: String,
    val todayLow: String,
    val currentTemp: String,
    val todayShortForecast: String,
    val todayDetailedForecast: String,
)
