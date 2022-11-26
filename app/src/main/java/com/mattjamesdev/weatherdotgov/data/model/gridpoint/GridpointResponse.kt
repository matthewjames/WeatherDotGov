package com.mattjamesdev.weatherdotgov.data.model.gridpoint


import com.google.gson.annotations.SerializedName
import com.mattjamesdev.weatherdotgov.domain.model.GridpointData
import com.mattjamesdev.weatherdotgov.domain.model.LatLong

data class GridpointResponse(
    @SerializedName("geometry")
    val geometry: Geometry?,
    @SerializedName("id")
    val id: String?,
)

fun GridpointResponse.toDomain(): GridpointData {
    this.geometry?.let { geometry ->
        geometry.coordinates?.let { coordinates ->
            coordinates.get(0)?.let { list ->
                val points = list.mapIndexedNotNull { index, list2 ->
                    list2?.let {
                        LatLong(it.get(1) ?: 0.0, it.get(0) ?: 0.0)
                    }
                }

                return GridpointData(
                    points = points
                )
            }
        }
    }

    return GridpointData.NULL
}