package com.mattjamesdev.weatherdotgov.network.model

data class DayForecast(
    val date: String,
    val periods: MutableList<Period>
)