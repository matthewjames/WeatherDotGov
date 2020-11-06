package com.mattjamesdev.weatherdotgov.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager

import com.mattjamesdev.weatherdotgov.R
import com.mattjamesdev.weatherdotgov.databinding.FragmentSevenDayBinding
import com.mattjamesdev.weatherdotgov.model.ForecastData
import com.mattjamesdev.weatherdotgov.view.adapter.SevenDayAdapter
import com.mattjamesdev.weatherdotgov.viewmodel.SearchActivityViewModel

/**
 * A simple [Fragment] subclass.
 */
class SevenDayFragment : Fragment() {
    private val TAG = "SevenDayFragment"
    private lateinit var viewModel: SearchActivityViewModel
    private lateinit var binding: FragmentSevenDayBinding
    private lateinit var hourlyForecastData: ForecastData

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_seven_day, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(SearchActivityViewModel::class.java)

        viewModel.isLoading.observe(viewLifecycleOwner, { isLoading ->
            if(isLoading){
                binding.rlSevenDayFragment.visibility = View.INVISIBLE
            }
        })

        viewModel.hourlyForecastData.observe(viewLifecycleOwner, { newHourlyForecastData ->
            hourlyForecastData = newHourlyForecastData
        })

        viewModel.dailyForecastData.observe(viewLifecycleOwner,{ dayForecastList ->
            // Populate 7 Day tab with data
            binding.rvSevenDay.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = SevenDayAdapter(requireContext(), dayForecastList, hourlyForecastData, viewModel.mLongitude, viewModel.mLatitude)
            }

            binding.rlSevenDayFragment.visibility = View.VISIBLE
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvSevenDay.addItemDecoration(DividerItemDecoration(view.context, DividerItemDecoration.VERTICAL))

    }
}
