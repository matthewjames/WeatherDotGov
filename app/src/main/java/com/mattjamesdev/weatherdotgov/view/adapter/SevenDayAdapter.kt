package com.mattjamesdev.weatherdotgov.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mattjamesdev.weatherdotgov.R
import com.mattjamesdev.weatherdotgov.network.model.DayForecast
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_sevenday.view.*

class SevenDayAdapter(val forecastData: MutableList<DayForecast>): RecyclerView.Adapter<SevenDayViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SevenDayViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_sevenday, parent, false)
        return SevenDayViewHolder(view)
    }

    override fun getItemCount(): Int {
        return forecastData.size
    }

    override fun onBindViewHolder(holder: SevenDayViewHolder, position: Int) {
        return holder.bind(forecastData[position])
    }
}

class SevenDayViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
    fun bind(dayForecast: DayForecast){
        itemView.tvDay.text = dayForecast.periods[0].name
        itemView.tvShortForecast.text = dayForecast.periods[0].shortForecast

        if (dayForecast.periods.size > 1) {
            itemView.tvHighTemp.text = "${dayForecast.periods[0].temperature.toString()}\u00B0${dayForecast.periods[0].temperatureUnit}"
            itemView.tvLowTemp.text = "${dayForecast.periods[1].temperature.toString()}\u00B0${dayForecast.periods[0].temperatureUnit}"
        } else {
            itemView.tvHighTemp.text = "--"
            itemView.tvLowTemp.text = "${dayForecast.periods[0].temperature.toString()}\u00B0${dayForecast.periods[0].temperatureUnit}"
        }

        Picasso.get().load(dayForecast.periods[0].icon).into(itemView.ivForecastIcon)
    }
}