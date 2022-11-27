package com.mattjamesdev.weatherdotgov.domain.model

data class GridpointData(
    val points: List<LatLong>
) {
    companion object {
        val NULL = GridpointData(
            points = listOf(LatLong.EMPTY)
        )
    }
}
