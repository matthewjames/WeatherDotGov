package com.mattjamesdev.weatherdotgov.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider

import com.mattjamesdev.weatherdotgov.R
import com.mattjamesdev.weatherdotgov.databinding.FragmentTodayBinding
import com.mattjamesdev.weatherdotgov.viewmodel.SearchActivityViewModel
import kotlinx.android.synthetic.main.fragment_today.*


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

        viewModel.cityState.observe(viewLifecycleOwner, { newCityState ->
            binding.tvPointForecastCityState.text = newCityState
        })

        viewModel.pointForecastLatLong.observe(viewLifecycleOwner, { newLatLong ->
            binding.tvPointForecastLatLong.text = newLatLong
        })

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Sets main linear layout to fill full screen
        view.viewTreeObserver.addOnGlobalLayoutListener {
            if (view.height > 0){
                val params = llMain.layoutParams
                params.height = view.height
                llMain.layoutParams = params
            }
        }

        val supportMapFragment = childFragmentManager.findFragmentById(R.id.mapContainer) as MapFragment
        val listener = object : MapFragment.OnTouchListener {
            override fun onTouch() {
                svTodayFragment.requestDisallowInterceptTouchEvent(true)
            }
        }

        supportMapFragment.setListener(listener)
    }
}
