package com.mattjamesdev.weatherdotgov.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider

import com.mattjamesdev.weatherdotgov.R
import com.mattjamesdev.weatherdotgov.databinding.FragmentTomorrowBinding
import com.mattjamesdev.weatherdotgov.domain.model.DayForecast
import com.mattjamesdev.weatherdotgov.utils.TemperatureGraph
import com.mattjamesdev.weatherdotgov.viewmodel.SearchActivityViewModel
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.fragment_tomorrow.*
import kotlinx.android.synthetic.main.fragment_tomorrow.view.*
import kotlinx.android.synthetic.main.item_sevenday.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class TomorrowFragment : Fragment() {
    private lateinit var binding: FragmentTomorrowBinding
    private lateinit var viewModel: SearchActivityViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_tomorrow, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(SearchActivityViewModel::class.java)

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.rlTomorrowFragment.visibility = View.INVISIBLE
            }
        }

        viewModel.dailyForecastData.observe(viewLifecycleOwner) { dayForecastList ->
            if(dayForecastList[0].date != null){
                bindDailyForecastData(dayForecastList)
            }
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

    }

    private fun bindDailyForecastData(dayForecastList: MutableList<DayForecast>){
        val tomorrowForecast = dayForecastList[1]
        val tempUnit = tomorrowForecast.tempUnit

        binding.tvTomorrowDate.text = DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.getDefault())
            .format(LocalDate.now().plusDays(1))
        binding.tvTomorrowHigh.text = getString(R.string.high_temp, tomorrowForecast.high?.temperature, tempUnit)
        binding.tvTomorrowLow.text = getString(R.string.low_temp, tomorrowForecast.low?.temperature, tempUnit)
        binding.tvTomorrowShortForecast.text = tomorrowForecast.high?.shortForecast
        binding.tvTomorrowDetailedForecast.text = tomorrowForecast.high?.detailedForecast
        Picasso.get().load(tomorrowForecast.high?.icon?.replaceAfter("=", "large")).into(binding.ivTomorrowIcon)

        val periods = tomorrowForecast.hourly!!
        TemperatureGraph(requireContext(), periods, tomorrowHourlyChart).build()

        binding.svTomorrowChart.scrollTo(0,0)

        binding.rlTomorrowFragment.visibility = View.VISIBLE
    }
}
