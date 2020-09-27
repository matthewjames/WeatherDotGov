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
        val day = dayForecast.high!!.name
        val shortForecast = dayForecast.high!!.shortForecast
        val highTemp = dayForecast.high!!.temperature
        val lowTemp = dayForecast.low!!.temperature
        val iconPath = dayForecast.high!!.icon.replaceAfter("=", "large")
        val tempUnit = dayForecast.tempUnit!!

        itemView.tvDay.text = day
        itemView.tvShortForecast.text = shortForecast
        itemView.tvHighTemp.text = "$highTemp\u00B0$tempUnit"
        itemView.tvLowTemp.text = "$lowTemp\u00B0$tempUnit"

        Picasso.get().load(iconPath).into(itemView.ivForecastIcon)
    }
}