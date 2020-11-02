package com.mattjamesdev.weatherdotgov.view

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolygonOptions
import com.mattjamesdev.weatherdotgov.R
import com.mattjamesdev.weatherdotgov.viewmodel.SearchActivityViewModel

class MapFragment : Fragment() {
    private val TAG = "MapFragment"
    private lateinit var viewModel: SearchActivityViewModel
    private var polygonPoints = listOf<LatLng>()

    private val callback = OnMapReadyCallback { googleMap ->
        viewModel.gridpointData.observe(this,{
            // Maps array of array of coordinates to List<LatLng>
            polygonPoints = it.geometry.coordinates[0].mapIndexed{ index, list -> LatLng(list[1],list[0]) }
            Log.d(TAG, "Polygon points: $polygonPoints")

            val polygon1 = googleMap.addPolygon(
                PolygonOptions()
                    .addAll(polygonPoints)
                    .strokeWidth(2f)
                    .strokeColor(resources.getColor(R.color.polyExterior, context?.theme))
                    .fillColor(resources.getColor(R.color.polyInterior, context?.theme))
            )

            val boundsBuilder = LatLngBounds.Builder()
            for (point in polygonPoints){
                boundsBuilder.include(point)
            }
            val bounds = boundsBuilder.build()
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bounds.center, 12f))
        })
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate() entered")

        viewModel = ViewModelProvider(requireActivity()).get(SearchActivityViewModel::class.java)


        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
}