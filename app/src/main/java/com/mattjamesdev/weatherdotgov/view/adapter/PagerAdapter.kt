package com.mattjamesdev.weatherdotgov.view.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.mattjamesdev.weatherdotgov.view.TenDayFragment
import com.mattjamesdev.weatherdotgov.view.TodayFragment
import com.mattjamesdev.weatherdotgov.view.TomorrowFragment

class PagerAdapter(fragmentManager: FragmentManager, val numberOfTabs: Int) : FragmentPagerAdapter(fragmentManager)  {
    override fun getItem(position: Int): Fragment {
        when(position){
            0 -> {
                return TodayFragment()
            }
            1 -> {
                return TomorrowFragment()
            }
            else -> return TenDayFragment()

        }
    }

    override fun getCount(): Int {
        return numberOfTabs
    }
}