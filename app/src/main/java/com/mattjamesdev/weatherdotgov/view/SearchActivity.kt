package com.mattjamesdev.weatherdotgov.view

import android.content.Context
import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.mattjamesdev.weatherdotgov.R
import com.mattjamesdev.weatherdotgov.view.adapter.PagerAdapter
import com.mattjamesdev.weatherdotgov.view.adapter.SevenDayAdapter
import com.mattjamesdev.weatherdotgov.viewmodel.SearchActivityViewModel
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.fragment_seven_day.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*

class SearchActivity : AppCompatActivity() {

    private lateinit var viewModel: SearchActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initViewModelComponents()
        initSearchButtons()
        initTabLayout()
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
            rvSevenDay.apply {
                layoutManager = LinearLayoutManager(this.context)
                adapter = SevenDayAdapter(it)
            }
        })
    }

    private fun initTabLayout(){
        val pagerAdapter = PagerAdapter(supportFragmentManager, tabLayout.tabCount)
        viewPager.adapter = pagerAdapter
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        viewPager.offscreenPageLimit = pagerAdapter.numberOfTabs

        tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener{
            override fun onTabSelected(p0: TabLayout.Tab?) {
                viewPager.currentItem = p0!!.position
            }

            override fun onTabReselected(p0: TabLayout.Tab?) {
            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
            }
        })
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
