package com.mattjamesdev.weatherdotgov.model

data class DayForecast(
    var date: String? = null,
    var hourly: List<Period>? = null,
    var high: Period? = null,
    var low: Period? = null,
    var tempUnit: String? = null,
    var isExpanded: Boolean = false,
    var hasAlert: Boolean = false
)