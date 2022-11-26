package com.mattjamesdev.weatherdotgov.view

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View.*
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.common.api.Status
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.mattjamesdev.weatherdotgov.Keys
import com.mattjamesdev.weatherdotgov.R
import com.mattjamesdev.weatherdotgov.databinding.ActivitySearchBinding
import com.mattjamesdev.weatherdotgov.domain.model.LatLong
import com.mattjamesdev.weatherdotgov.view.adapter.PagerAdapter
import com.mattjamesdev.weatherdotgov.viewmodel.SearchActivityViewModel
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SearchActivity : AppCompatActivity() {
    private val TAG = "SearchActivity"
    private val LOCATION_REQUEST_CODE = 1310
    private val AUTO_COMPLETE_REQUEST_CODE = 503
    private val viewModel: SearchActivityViewModel by viewModels()
    private lateinit var locationClient: FusedLocationProviderClient
    private lateinit var binding: ActivitySearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search)
        locationClient = LocationServices.getFusedLocationProviderClient(this)

        initViewModelComponents()
        initSearchBar()
        initTabLayout()
        getLocation()

        lifecycleScope.launch {
            viewModel.location.collectLatest { latLong ->
                if(latLong.isSet){
                    search(latLong)
                }
            }
        }

        lifecycleScope.launch {
            viewModel.errorMessage.collect{
                showErrorSnackbar(it)
            }
        }
    }

    private fun getLocation(){
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermission()
            return
        }
        locationClient.lastLocation.addOnSuccessListener { location ->
            Log.d(TAG, "locationClient.lastlocation = $location")
            viewModel.setLocation(
                LatLong(
                    lat = location.latitude,
                    long = location.longitude
                )
            )
        }
        locationClient.lastLocation.addOnFailureListener {
            showErrorSnackbar("Error getting devices location: ${it.message}")
        }
    }

    private fun showErrorSnackbar(message: String){
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
    }

    private fun requestLocationPermission(){
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            LOCATION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if(requestCode == LOCATION_REQUEST_CODE){
            if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                // Permission granted
                getLocation()
            } else {
                // Permission denied
                Toast.makeText(this, "Current location cannot be determined: Location Permission Denied", Toast.LENGTH_LONG).show()
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun initViewModelComponents(){
        // progress bar
        viewModel.isLoading.observe(this) {
            if (it) {
                progressBar.visibility = VISIBLE
                binding.cityState = ""
            } else {
                binding.viewPager.adapter?.notifyDataSetChanged()
                binding.viewPager.setCurrentItem(0, true)
                progressBar.visibility = GONE
            }
        }
    }

    private fun initTabLayout(){
        val tabTitles = listOf(
            getString(R.string.tab_today),
            getString(R.string.tab_tomorrow),
            getString(R.string.tab_7_day)
        )
        val pagerAdapter = PagerAdapter(this, tabLayout.tabCount)
        binding.viewPager.adapter = pagerAdapter
        binding.viewPager.offscreenPageLimit = pagerAdapter.numberOfTabs


        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitles[position]
            binding.viewPager.setCurrentItem(tab.position, true)
        }.attach()
    }

    private fun initSearchBar(){
        binding.etLocation.focusable = NOT_FOCUSABLE

        lifecycleScope.launch {
            viewModel.cityState.collect { cityState ->
                binding.cityState = resources.getString(R.string.search_city_state, cityState.city, cityState.state)
            }
        }

        // search bar button
        binding.ivSearch.setOnClickListener {
//            lifecycleScope.launch {
//                viewModel.location.collectLatest {
//                    if (it != LatLong.EMPTY) {
//                        search(it)
//                    }
//                }
//            }
        }

        // Keyboard search button
//        binding.etLocation.setOnEditorActionListener { v, actionId, event ->
//            if(actionId == EditorInfo.IME_ACTION_SEARCH){
//                lifecycleScope.launch {
//                    viewModel.location.collectLatest {
//                        search(it)
//                    }
//                }
//                true
//            } else {
//                false
//            }
//        }

        // autocomplete functionality using Places SDK

        Places.initialize(applicationContext, Keys.placesKey())

        binding.etLocation.setOnClickListener {
            val fieldList = arrayListOf(Place.Field.LAT_LNG)
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fieldList).build(this)
            startActivityForResult(intent, AUTO_COMPLETE_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        data?.let { intentData ->
            if(requestCode == AUTO_COMPLETE_REQUEST_CODE && resultCode == Activity.RESULT_OK){
                val place: Place = Autocomplete.getPlaceFromIntent(intentData)

                place.latLng?.let { latLng ->
                    val autoCompleteLocation = LatLong(
                        lat = latLng.latitude,
                        long = latLng.longitude
                    )
                    if (autoCompleteLocation.isSet) {
                        viewModel.setLocation(autoCompleteLocation)
                    }
                }
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR){
                val status: Status = Autocomplete.getStatusFromIntent(intentData)
                showErrorSnackbar("Error: ${status.statusMessage}")
            } else {
                return
            }
        }
    }

    private fun search(latLong: LatLong){
        Log.d(TAG, "fetching ForecastAreaV2 with $latLong")
        viewModel.fetchForecastAreaV2(latLong)

        // dismiss keyboard
        val view = currentFocus
        if (view != null) {
            val imm = this@SearchActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }
}
