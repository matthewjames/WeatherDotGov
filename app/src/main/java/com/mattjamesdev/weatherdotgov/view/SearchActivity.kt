package com.mattjamesdev.weatherdotgov.view

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Typeface
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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
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
import com.mattjamesdev.weatherdotgov.model.DayForecast
import com.mattjamesdev.weatherdotgov.model.ForecastData
import com.mattjamesdev.weatherdotgov.model.Period
import com.mattjamesdev.weatherdotgov.utils.MyValueFormatter
import com.mattjamesdev.weatherdotgov.utils.TemperatureGraph
import com.mattjamesdev.weatherdotgov.view.adapter.PagerAdapter
import com.mattjamesdev.weatherdotgov.view.adapter.SevenDayAdapter
import com.mattjamesdev.weatherdotgov.viewmodel.SearchActivityViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.fragment_seven_day.*
import kotlinx.android.synthetic.main.fragment_today.*
import kotlinx.android.synthetic.main.fragment_tomorrow.*
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList

class SearchActivity : AppCompatActivity() {
    private val TAG = "SearchActivity"
    private val LOCATION_REQUEST_CODE = 1310
    private val AUTO_COMPLETE_REQUEST_CODE = 503
    var mLatitude: Double = 0.0
    var mLongitude: Double = 0.0
    private lateinit var hourlyForecastData: ForecastData
    private lateinit var viewModel: SearchActivityViewModel
    private lateinit var locationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        locationClient = LocationServices.getFusedLocationProviderClient(this)

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            getLastLocation()
        } else {
            requestLocationPermission()
        }

        initViewModelComponents()
        initSearchBar()
        initTabLayout()
    }

    private fun getLastLocation(){
        locationClient.lastLocation.addOnSuccessListener {location: Location? ->
            if (location != null){
                mLatitude = location.latitude
                mLongitude = location.longitude
//                Log.d(TAG, "Location coordinates: $mLatitude, $mLongitude")
                viewModel.getForecastData(mLatitude, mLongitude)
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
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                // Permission granted
                getLastLocation()
            } else {
                // Permission denied
                Toast.makeText(this, "Current location cannot be determined: Permission Denied", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun initViewModelComponents(){
        viewModel = ViewModelProvider(this).get(SearchActivityViewModel::class.java)

        // progress bar
        viewModel.isLoading.observe(this, {
            if(it){
                progressBar.visibility = VISIBLE
            } else {
                progressBar.visibility = GONE
            }
        })

        viewModel.dailyForecastData.observe(this, {

            updateTodayTab(it[0])
            updateTomorrowTab(it[1])

            // Populate 7 Day tab with data
            rvSevenDay.apply {
                layoutManager = LinearLayoutManager(this.context)
                adapter = SevenDayAdapter(this.context, it, hourlyForecastData, mLongitude, mLatitude)
            }

            viewPager.setCurrentItem(0, true)
        })

        viewModel.hourlyForecastData.observe(this, {
            hourlyForecastData = it
        })

        viewModel.errorMessage.observe(this, {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })

        viewModel.cityState.observe(this, {
            etLocation.setText(it)
        })
    }

    private fun initTabLayout(){
        val tabTitles = listOf(
            getString(R.string.tab_today),
            getString(R.string.tab_tomorrow),
            getString(R.string.tab_7_day)
        )
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
            search(mLatitude, mLongitude)
        }

        // keyboard button
        etLocation.setOnEditorActionListener { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_SEARCH){
                search(mLatitude, mLongitude)
                true
            } else {
                false
            }
        }

        // autocomplete functionality using Places SDK

        Places.initialize(applicationContext, Keys.placesKey())

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
            mLatitude = place.latLng!!.latitude
            mLongitude = place.latLng!!.longitude

            search(mLatitude, mLongitude)
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
        tvTodayHigh.text = getString(R.string.high_temp ,todayForecast.high?.temperature, tempUnit)
        tvTodayLow.text = getString(R.string.low_temp, todayForecast.low?.temperature, tempUnit)
        tvCurrentTemp.text = getString(R.string.temp, currentForecast.temperature, tempUnit)
        tvTodayShortForecast.text = currentForecast.shortForecast

        Picasso.get().load(currentForecast.icon.replaceAfter("=", "large")).into(ivTodayIcon)

//        Log.d(TAG, "hourlyForecastData: ${hourlyForecastData.properties.periods.subList(0, 24)}")

        val periods = hourlyForecastData.properties.periods.subList(0, 24)
        TemperatureGraph(this, periods, todayHourlyChart).build()

        svTodayChart.scrollTo(0,0)

        viewPager.adapter?.notifyDataSetChanged()

        rlTodayFragment.visibility = VISIBLE
    }

    private fun updateTomorrowTab(tomorrowForecast: DayForecast){
        val tomorrowDate: String = DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.getDefault())
                                                    .format(LocalDate.now().plusDays(1))
        val tempUnit = tomorrowForecast.tempUnit

        tvTomorrowDate.text = tomorrowDate
        tvTomorrowHigh.text = getString(R.string.high_temp, tomorrowForecast.high?.temperature, tempUnit)
        tvTomorrowLow.text = getString(R.string.low_temp, tomorrowForecast.low?.temperature, tempUnit)
        tvTomorrowShortForecast.text = tomorrowForecast.high?.shortForecast
        Picasso.get().load(tomorrowForecast.high?.icon?.replaceAfter("=", "large")).into(ivTomorrowIcon)

        val periods = tomorrowForecast.hourly!!
        TemperatureGraph(this, periods, tomorrowHourlyChart).build()

        svTomorrowChart.scrollTo(0,0)

        viewPager.adapter?.notifyDataSetChanged()

        rlTomorrowFragment.visibility = VISIBLE
    }

    private fun search(latitude: Double, longitude: Double){
        viewModel.getForecastData(latitude, longitude)

        // dismiss keyboard
        val view = currentFocus
        if (view != null) {
            val imm = this@SearchActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}
