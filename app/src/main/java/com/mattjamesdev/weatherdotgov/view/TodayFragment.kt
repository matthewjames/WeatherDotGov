package com.mattjamesdev.weatherdotgov.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider

import com.mattjamesdev.weatherdotgov.R
import com.mattjamesdev.weatherdotgov.viewmodel.SearchActivityViewModel
import kotlinx.android.synthetic.main.fragment_today.*
import kotlinx.android.synthetic.main.fragment_today.view.*


class TodayFragment : Fragment() {
    private lateinit var viewModel: SearchActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this).get(SearchActivityViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_today, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.viewTreeObserver.addOnGlobalLayoutListener {
            if (view.height > 0){
                val params = llMain.layoutParams
                params.height = view.height
                llMain.layoutParams = params
            }
        }
    }
}
