package com.mattjamesdev.weatherdotgov.domain.model

data class CityState(
    val city: String,
    val state: String
){
    companion object {
        val NULL = CityState(
            city = "",
            state = ""
        )
    }
}
