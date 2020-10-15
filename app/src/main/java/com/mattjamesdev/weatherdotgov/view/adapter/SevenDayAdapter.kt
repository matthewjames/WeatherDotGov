package com.mattjamesdev.weatherdotgov.view.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.mattjamesdev.weatherdotgov.R
import com.mattjamesdev.weatherdotgov.model.DayForecast
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_sevenday.view.*
import kotlinx.android.synthetic.main.item_sevenday_button.view.*

class SevenDayAdapter(val context: Context, val forecastData: MutableList<DayForecast>, val longitude: Double, val latitude: Double, val width: Int): RecyclerView.Adapter<SevenDayViewHolder>(){
    val TAG = "SevenDayAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SevenDayViewHolder {
        Log.d(TAG, "onCreateViewHolder() entered")

        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return SevenDayViewHolder(view, width)
    }

    override fun getItemCount(): Int {
        return forecastData.size + 1
    }

    override fun onBindViewHolder(holder: SevenDayViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder() entered")


        return if(position == forecastData.size) {
            holder.itemView.button_launch_website.setOnClickListener {
                val url = "https://forecast.weather.gov/MapClick.php?lat=$latitude&lon=$longitude"
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)
                context.startActivity(intent)
            }
        } else {
            val expanded = forecastData[position].isExpanded
            holder.expandableLayout.visibility = if(expanded) View.VISIBLE else View.GONE

            holder.itemView.rlDayForecast.setOnClickListener {
                val dayForecast = forecastData[position]
                dayForecast.isExpanded = !dayForecast.isExpanded
                notifyItemChanged(position)
            }

            holder.itemView.flItemBackgroundImage.minimumWidth = width
            holder.bind(forecastData[position])
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == forecastData.size) R.layout.item_sevenday_button else R.layout.item_sevenday
    }
}

class SevenDayViewHolder(itemView: View, val width: Int): RecyclerView.ViewHolder(itemView){
    val expandableLayout = itemView.rlExpandableLayout

    fun bind(dayForecast: DayForecast){
        val day = dayForecast.high!!.name
        val shortForecast = dayForecast.high!!.shortForecast
        val highTemp = dayForecast.high!!.temperature
        val lowTemp = dayForecast.low!!.temperature
        val iconUrl = dayForecast.high!!.icon.replaceAfter("=", "large")
        val tempUnit = dayForecast.tempUnit!!

        itemView.tvDay.text = day
        itemView.tvShortForecast.text = shortForecast
        itemView.tvHighTemp.text = "$highTemp\u00B0$tempUnit"
        itemView.tvLowTemp.text = "$lowTemp\u00B0$tempUnit"


        itemView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)

        Log.d("bind", "Width: $width")
        Picasso.get().load(iconUrl)
            .fit()
            .into(itemView.ivForecastIcon)
    }
}