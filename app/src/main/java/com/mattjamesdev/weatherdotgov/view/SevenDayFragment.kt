package com.mattjamesdev.weatherdotgov.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration

import com.mattjamesdev.weatherdotgov.R
import kotlinx.android.synthetic.main.fragment_seven_day.*

/**
 * A simple [Fragment] subclass.
 */
class SevenDayFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_seven_day, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvSevenDay.addItemDecoration(DividerItemDecoration(view.context, DividerItemDecoration.VERTICAL))

    }
}
