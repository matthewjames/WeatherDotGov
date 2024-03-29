package com.mattjamesdev.weatherdotgov.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.mattjamesdev.weatherdotgov.R
import com.mattjamesdev.weatherdotgov.data.model.Period
import com.mattjamesdev.weatherdotgov.data.model.alerts.AlertsResponse
import com.mattjamesdev.weatherdotgov.databinding.FragmentTodayBinding
import com.mattjamesdev.weatherdotgov.domain.model.DayForecast
import com.mattjamesdev.weatherdotgov.utils.TemperatureGraph
import com.mattjamesdev.weatherdotgov.viewmodel.SearchActivityViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_today.*
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*


class TodayFragment : Fragment() {
    private val TAG = "TodayFragment"
    private lateinit var viewModel: SearchActivityViewModel
    private lateinit var binding: FragmentTodayBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_today, container, false)

        viewModel = ViewModelProvider(requireActivity()).get(SearchActivityViewModel::class.java)

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.rlTodayFragment.visibility = View.INVISIBLE
                binding.llAlert.visibility = View.GONE
                binding.cvAlertInfo.visibility = View.GONE
            }
        }

        viewModel.dailyForecastData.observe(viewLifecycleOwner) { dayForecastList ->
            // Updates TodayFragment UI with new data
            if (dayForecastList[0].date != null) {
                bindDailyforecastData(dayForecastList)
            }
        }

        lifecycleScope.launch {
            viewModel.hourlyForecastResponse.collect { hourlyForecastData ->
                val periods = hourlyForecastData.properties?.periods?.subList(0, 24)
                periods?.let {
                    TemperatureGraph(requireContext(), it, binding.todayHourlyChart).build()
                }
            }
        }

        lifecycleScope.launch {
            viewModel.cityState.collect { newCityState ->
                binding.cityState = newCityState
            }
        }

        lifecycleScope.launch {
            viewModel.pointForecastText.collect {
                binding.pointForecastLatLong = it
            }
        }

        viewModel.pointForecastLatLong.observe(viewLifecycleOwner) { newLatLong ->
            binding.tvPointForecastLatLong.text = newLatLong
        }

        viewModel.alertData.observe(viewLifecycleOwner) { newAlertData ->
            if (newAlertData.features?.isNotEmpty() == true) {
                showAlert(newAlertData)
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Sets main linear layout to fill full screen
        view.viewTreeObserver.addOnGlobalLayoutListener {
            if (view.height > 0) {
                val params = llMain.layoutParams
                params.height = view.height
                llMain.layoutParams = params
            }
        }

        val supportMapFragment =
            childFragmentManager.findFragmentById(R.id.mapContainer) as MapFragment
        val listener = object : MapFragment.OnTouchListener {
            override fun onTouch() {
                svTodayFragment.requestDisallowInterceptTouchEvent(true)
            }
        }

        supportMapFragment.setListener(listener)
    }

    private fun bindDailyforecastData(dayForecastList: MutableList<DayForecast>) {
        val todayForecast = dayForecastList[0]
        val currentForecast: Period = todayForecast.hourly?.get(0)!!
        val tempUnit = todayForecast.tempUnit

        binding.dateTimeText =
            DateTimeFormatter.ofPattern("MMMM d, h:mm a", Locale.getDefault())
                .format(LocalDateTime.now())
        binding.todayHighText =
            getString(R.string.high_temp, todayForecast.high?.temperature.toString(), tempUnit)
        binding.tvTodayLow.text =
            getString(R.string.low_temp, todayForecast.low?.temperature, tempUnit)
        binding.tvCurrentTemp.text =
            getString(R.string.temp, currentForecast.temperature, tempUnit)
        binding.tvTodayShortForecast.text = currentForecast.shortForecast
        binding.tvTodayDetailedForecast.text =
            if (currentForecast?.isDaytime == true) todayForecast.high?.detailedForecast else todayForecast.low?.detailedForecast

        Picasso.get().load(currentForecast.icon?.replaceAfter("=", "large"))
            .into(binding.ivTodayIcon)

//        Log.d(TAG, "hourlyForecastData: ${hourlyForecastData.properties.periods.subList(0, 24)}")

        binding.svTodayChart.scrollTo(0, 0)
        binding.svTodayFragment.scrollTo(0, 0)

        binding.rlTodayFragment.visibility = View.VISIBLE
    }

    private fun showAlert(alertData: AlertsResponse) {
        val alertProperties = alertData.features?.get(0)?.properties

        binding.llAlert.setOnClickListener {
            svTodayFragment.smoothScrollTo(0, cvAlertInfo.bottom)
        }
        binding.tvAlertEvent.text = alertProperties?.event
        binding.tvAlertHeadline.text = alertProperties?.headline
        binding.cvAlertInfo.setOnClickListener {
            val alertIsExpanded = binding.tvAlertDescription.visibility == View.VISIBLE
            val rotationDegree = if (alertIsExpanded) -90f else 90f

            binding.ivAlertArrow.animate().rotationBy(rotationDegree).setDuration(100).start()

            binding.tvAlertDescription.visibility = if (alertIsExpanded) View.GONE else View.VISIBLE
            binding.svTodayFragment.post {
                binding.svTodayFragment.smoothScrollTo(0, binding.cvAlertInfo.bottom)
            }
        }
        binding.tvAlertDescription.text = alertProperties?.description

        binding.llAlert.visibility = View.VISIBLE
        binding.cvAlertInfo.visibility = View.VISIBLE
    }
}
