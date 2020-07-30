package com.mattjamesdev.weatherdotgov.view

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
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
import kotlinx.android.synthetic.main.item_sevenday.*
import kotlinx.android.synthetic.main.item_sevenday.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class SearchActivity : AppCompatActivity() {
    private val TAG = "SearchActivity"
    private val LOCATION_REQUEST_CODE = 1310
    private lateinit var viewModel: SearchActivityViewModel
    private lateinit var locationClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        locationClient = LocationServices.getFusedLocationProviderClient(this)

        initViewModelComponents()
        initSearchButtons()
        initTabLayout()
//        initCurrentForecast()
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
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
            // populate Today tab with data
            val currentTime: String = SimpleDateFormat("MMMM d, h:mm a", Locale.getDefault()).format(Date())
            val todayForecast: DayForecast = it.get(0)
            val currentForecast: Period = todayForecast.hourly?.get(0)!!
            val tempUnit = todayForecast.tempUnit

//            tvTodayDateTime.text = currentTime
//            tvTodayHigh.text = "${todayForecast.high?.temperature}\u00B0$tempUnit"
//            tvLowTemp.text = "${todayForecast.low?.temperature}\u00B0$tempUnit"
//            tvCurrentTemp.text = "${currentForecast.temperature}\u00B0$tempUnit"
//            tvShortForecast.text = "${currentForecast.shortForecast}"
//            Picasso.get().load(currentForecast.icon).into(ivTodayIcon)

            // populate Tomorrow tab with data

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
//        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        viewPager.offscreenPageLimit = pagerAdapter.numberOfTabs

        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitles[position]
            viewPager.setCurrentItem(tab.position, true)
        }.attach()

//        tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
//            override fun onTabSelected(p0: TabLayout.Tab?) {
//                viewPager.currentItem = p0!!.position
//            }
//
//            override fun onTabReselected(p0: TabLayout.Tab?) {
//            }
//
//            override fun onTabUnselected(p0: TabLayout.Tab?) {
//            }
//        })
    }

    private fun initSearchButtons(){

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
    }

    private fun initCurrentForecast(){

        locationClient.lastLocation.addOnSuccessListener { location ->
            Log.d(TAG, "Location coordinates: ${location.latitude}, ${location.longitude}")

//            viewModel.getForecastData("${location.latitude},${location.longitude}")
        }
    }

    private fun loadFragment(){
        val firstFragment = TomorrowFragment()

        supportFragmentManager.beginTransaction()
            .replace(R.id.viewPager, firstFragment)
            .addToBackStack(null)
            .commit()
    }

    private fun search(){
        val coords = getCoords()

        viewModel.getForecastData(coords)


        // dismiss keyboard
        val view = currentFocus
        if (view != null) {
            val imm = this@SearchActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    private fun getCoords() : String {
        val geocoder = Geocoder(this, Locale.getDefault())
        val location = geocoder.getFromLocationName(etLocation.text.toString(), 1)

        if (location.size > 0) {
            val latitude = location.get(0).latitude
            val longitude = location.get(0).longitude
            val city = location.get(0).locality

            return "$latitude,$longitude"
        } else {
            Toast.makeText(this, "No results found. Try a different location.", Toast.LENGTH_LONG).show()
            println("Invalid location name entered")
            return ""
        }
    }
}
