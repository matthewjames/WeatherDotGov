package com.mattjamesdev.weatherdotgov.model

data class GridpointData (
    val geometry: Geometry
)

data class Geometry(
    val coordinates: List<List<List<Double>>>
)