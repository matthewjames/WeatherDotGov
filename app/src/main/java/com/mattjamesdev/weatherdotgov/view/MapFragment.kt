package com.mattjamesdev.weatherdotgov.view

import android.content.Context
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.lifecycle.ViewModelProvider

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolygonOptions
import com.mattjamesdev.weatherdotgov.R
import com.mattjamesdev.weatherdotgov.viewmodel.SearchActivityViewModel

class MapFragment : Fragment(), GoogleMap.OnMapClickListener {
    private val TAG = "MapFragment"
    private lateinit var viewModel: SearchActivityViewModel
    private lateinit var mListener: OnTouchListener

    private val callback = OnMapReadyCallback { googleMap ->
        viewModel.gridpointData.observe(this) {
            googleMap.clear()

            // Maps array of array of coordinates to List<LatLng>
            val polygonPoints =
                it.geometry.coordinates[0].mapIndexed { index, list -> LatLng(list[1], list[0]) }
            Log.d(TAG, "Polygon points: $polygonPoints")

            // Draw polygon from coordinates list
            googleMap.addPolygon(
                PolygonOptions()
                    .addAll(polygonPoints)
                    .strokeWidth(3f)
                    .strokeColor(resources.getColor(R.color.polyExterior, context?.theme))
                    .fillColor(resources.getColor(R.color.polyInterior, context?.theme))
            )

            // Use coordinates list to build a boundary for the map to locate and focus on
            val boundsBuilder = LatLngBounds.Builder()
            for (point in polygonPoints) {
                boundsBuilder.include(point)
            }
            val bounds = boundsBuilder.build()

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(bounds.center, 12f))
        }

        googleMap.setOnMapClickListener(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(SearchActivityViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val layout = inflater.inflate(R.layout.fragment_map, container, false)

        // Assists in giving high scroll/touch precedence to Map Fragment
        val frameLayout = TouchableWrapper(requireActivity())
        frameLayout.setBackgroundColor(resources.getColor(R.color.transparent, requireContext().theme))
        (layout as ViewGroup).addView(frameLayout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)

        return layout
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }

    override fun onMapClick(p0: LatLng) {
        viewModel.getForecastData(p0.latitude, p0.longitude)
    }


    // Everything below is to give higher scroll/touch precedence to Map Fragment over NestedScrollView container
    fun setListener(listener: OnTouchListener){
        mListener = listener
    }

    interface OnTouchListener {
        abstract fun onTouch()
    }

    inner class TouchableWrapper(context : Context) : FrameLayout(context) {
        override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
            when (ev?.action){
                MotionEvent.ACTION_DOWN -> mListener.onTouch()
                MotionEvent.ACTION_UP -> mListener.onTouch()
            }
            return super.dispatchTouchEvent(ev)
        }
    }
}