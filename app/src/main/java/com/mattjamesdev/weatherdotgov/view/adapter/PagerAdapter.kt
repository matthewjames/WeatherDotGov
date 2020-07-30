package com.mattjamesdev.weatherdotgov.view.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.mattjamesdev.weatherdotgov.view.SevenDayFragment
import com.mattjamesdev.weatherdotgov.view.TodayFragment
import com.mattjamesdev.weatherdotgov.view.TomorrowFragment

class PagerAdapter(fragmentActivity: FragmentActivity, val numberOfTabs: Int) : FragmentStateAdapter(fragmentActivity)  {
    override fun getItemCount(): Int = numberOfTabs

    override fun createFragment(position: Int): Fragment {
        when(position){
            0 -> {
                return TodayFragment()
            }
            1 -> {
                return TomorrowFragment()
            }
            else -> return SevenDayFragment()
        }
    }
}