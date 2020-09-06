package com.mattjamesdev.weatherdotgov.view

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View.*
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.tabs.TabLayoutMediator
import com.mattjamesdev.weatherdotgov.Keys
import com.mattjamesdev.weatherdotgov.R
import com.mattjamesdev.weatherdotgov.network.model.DayForecast
import com.mattjamesdev.weatherdotgov.network.model.Period
import com.mattjamesdev.weatherdotgov.view.adapter.PagerAdapter
import com.mattjamesdev.weatherdotgov.view.adapter.SevenDayAdapter
import com.mattjamesdev.weatherdotgov.viewmodel.SearchActivityViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.fragment_seven_day.*
import kotlinx.android.synthetic.main.fragment_today.*
import kotlinx.android.synthetic.main.fragment_tomorrow.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class SearchActivity : AppCompatActivity() {
    private val TAG = "SearchActivity"
    private val LOCATION_REQUEST_CODE = 1310
    private val AUTO_COMPLETE_REQUEST_CODE = 503
    private lateinit var geocoder: Geocoder
    private lateinit var viewModel: SearchActivityViewModel
    private lateinit var locationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        geocoder = Geocoder(this, Locale.getDefault())
        locationClient = LocationServices.getFusedLocationProviderClient(this)

        initViewModelComponents()
        initSearchBar()
        initTabLayout()
    }

    override fun onStart() {
        super.onStart()
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            getLastLocation()
        } else {
            requestLocationPermission()
        }
    }

    private fun getLastLocation(){
        locationClient.lastLocation.addOnSuccessListener {location: Location? ->
            if (location != null){
                Log.d(TAG, "Location coordinates: ${location.latitude}, ${location.longitude}")
                etLocation.setText(getCityStateFromCoords(location.latitude, location.longitude))
                viewModel.getForecastData("${location.latitude},${location.longitude}")
            } else {
                Log.d(TAG, "Location was null")
            }
        }
    }

    private fun requestLocationPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
            Log.d(TAG, "requestLocationPermission: show dialog...")
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE)
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode == LOCATION_REQUEST_CODE){
            if(grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                // Permission granted
                getLastLocation()
            } else {
                // Permission denied
                Toast.makeText(this, "Current location cannot be determined: Permission Denied", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun initViewModelComponents(){
        viewModel = ViewModelProviders.of(this).get(SearchActivityViewModel::class.java)

        // progress bar
        viewModel.isLoading.observe(this, Observer {
            if(it){
                progressBar.visibility = VISIBLE
            } else {
                progressBar.visibility = GONE
            }
        })

        viewModel.dailyForecastData.observe(this, Observer {

            updateTodayTab(it.get(0))
            updateTomorrowTab(it.get(1))

            // Populate 7 Day tab with data
            rvSevenDay.apply {
                layoutManager = LinearLayoutManager(this.context)
                adapter = SevenDayAdapter(it)
            }
        })
    }

    private fun initTabLayout(){
        val tabTitles = listOf("Today", "Tomorrow", "7 Day")
        val pagerAdapter = PagerAdapter(this, tabLayout.tabCount)
        viewPager.adapter = pagerAdapter
        viewPager.offscreenPageLimit = pagerAdapter.numberOfTabs

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitles[position]
            viewPager.setCurrentItem(tab.position, true)
        }.attach()
    }

    private fun initSearchBar(){
        etLocation.focusable = NOT_FOCUSABLE

        // search bar button
        ivSearch.setOnClickListener {
            search()
        }

        // keyboard button
        etLocation.setOnEditorActionListener { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_SEARCH){
                search()
                true
            } else {
                false
            }
        }

        // autocomplete functionality using Places SDK

        Places.initialize(applicationContext, Keys.apiKey())

        etLocation.setOnClickListener {
            val fieldList = arrayListOf(Place.Field.LAT_LNG)
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).build(this)
            startActivityForResult(intent, AUTO_COMPLETE_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == AUTO_COMPLETE_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            val place: Place = Autocomplete.getPlaceFromIntent(data!!)
            val city: String = geocoder.getFromLocation(place.latLng!!.latitude, place.latLng!!.longitude, 1)[0].locality.toString()
            val state: String = geocoder.getFromLocation(place.latLng!!.latitude, place.latLng!!.longitude, 1)[0].adminArea.toString()
            val cityState: String = "$city, $state"
            Log.d(TAG, "onActivityResult: locality = $city, subLocality = $state")

            etLocation.setText(cityState)
            search()
        } else if (resultCode == AutocompleteActivity.RESULT_ERROR){
            val status: Status = Autocomplete.getStatusFromIntent(data!!)
            Toast.makeText(applicationContext, "Error: ${status.statusMessage}", Toast.LENGTH_LONG).show()
        }
    }

    private fun updateTodayTab(todayForecast: DayForecast){
        val currentDateTime: String = DateTimeFormatter.ofPattern("MMMM d, h:mm a", Locale.getDefault())
                                                        .format(LocalDateTime.now())
        val currentForecast: Period = todayForecast.hourly?.get(0)!!
        val tempUnit = todayForecast.tempUnit

        tvTodayDateTime.text = currentDateTime
        tvTodayHigh.text = "${todayForecast.high?.temperature}\u00B0$tempUnit"
        tvTodayLow.text = "${todayForecast.low?.temperature}\u00B0$tempUnit"
        tvCurrentTemp.text = "${currentForecast.temperature}\u00B0$tempUnit"
        tvTodayShortForecast.text = "${currentForecast.shortForecast}"
        Picasso.get().load(currentForecast.icon).into(ivTodayIcon)

        viewPager.adapter?.notifyDataSetChanged()

        rlTodayFragment.visibility = VISIBLE
    }

    private fun updateTomorrowTab(tomorrowForecast: DayForecast){
        val tomorrowDate: String = DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.getDefault())
                                                    .format(LocalDate.now().plusDays(1))
        val tempUnit = tomorrowForecast.tempUnit

        tvTomorrowDate.text = tomorrowDate
        tvTomorrowHigh.text = "${tomorrowForecast.high?.temperature}\u00B0$tempUnit"
        tvTomorrowLow.text = "${tomorrowForecast.low?.temperature}\u00B0$tempUnit"
        tvTomorrowShortForecast.text = "${tomorrowForecast.high?.shortForecast}"
        Picasso.get().load(tomorrowForecast.high?.icon).into(ivTomorrowIcon)

        viewPager.adapter?.notifyDataSetChanged()

        rlTomorrowFragment.visibility = VISIBLE
    }

    private fun search(){
        val coords = getCoords(etLocation.text.toString())

        viewModel.getForecastData(coords)


        // dismiss keyboard
        val view = currentFocus
        if (view != null) {
            val imm = this@SearchActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun getCityStateFromCoords(lat: Double, long: Double) : String {
        val cityState = geocoder.getFromLocation(lat, long, 1)

        if(cityState.size > 0){
            Log.d(TAG, "City, State: ${cityState.get(0).locality}, ${cityState.get(0).adminArea}")
            return "${cityState.get(0).locality}, ${cityState.get(0).adminArea}"
        } else {
            return "Unknown Location"
        }

    }

    private fun getCoords(inputText: String) : String {
        val location = geocoder.getFromLocationName(inputText, 1)

        if (location.size > 0) {
            val latitude = location.get(0).latitude
            val longitude = location.get(0).longitude

            return "$latitude,$longitude"
        } else {
            Toast.makeText(this, "No results found. Try a different location.", Toast.LENGTH_LONG).show()
            println("Invalid location name entered")
            return ""
        }
    }
}
