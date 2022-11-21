package com.mattjamesdev.weatherdotgov.view.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mattjamesdev.weatherdotgov.R
import com.mattjamesdev.weatherdotgov.data.model.Period
import com.mattjamesdev.weatherdotgov.data.model.hourly.HourlyForecastResponse
import com.mattjamesdev.weatherdotgov.databinding.ItemSevendayBinding
import com.mattjamesdev.weatherdotgov.domain.model.DayForecast
import com.mattjamesdev.weatherdotgov.utils.TemperatureGraph
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.item_sevenday.view.*
import kotlinx.android.synthetic.main.item_sevenday_button.view.*

class SevenDayAdapter(
    val context: Context,
    val forecastData: MutableList<DayForecast>,
    val hourlyForecastData: HourlyForecastResponse,
    val longitude: Double,
    val latitude: Double
) : RecyclerView.Adapter<SevenDayViewHolder>() {
    val TAG = "SevenDayAdapter"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SevenDayViewHolder {
        Log.d(TAG, "onCreateViewHolder() entered")

        val binding =
            ItemSevendayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        return SevenDayViewHolder(view, binding)
    }

    override fun getItemCount(): Int {
        return forecastData.size + 1
    }

    override fun onBindViewHolder(holder: SevenDayViewHolder, position: Int) {
        Log.d(TAG, "onBindViewHolder() entered")

        return if (position == forecastData.size) {
            holder.itemView.button_launch_website.setOnClickListener {
                val url = "https://forecast.weather.gov/MapClick.php?lat=$latitude&lon=$longitude"
                val intent = Intent(Intent.ACTION_VIEW)
                intent.data = Uri.parse(url)
                context.startActivity(intent)
            }
        } else {
            val dayForecast = forecastData[position]
            val expanded = dayForecast.isExpanded

            holder.bind(dayForecast)
            holder.expandableLayout.visibility = if (expanded) View.VISIBLE else View.GONE
            holder.itemView.rlDayForecast.setOnClickListener {
                dayForecast.isExpanded = !dayForecast.isExpanded
                notifyItemChanged(position)
            }

            val periods = if (position == 0) hourlyForecastData.properties?.periods?.subList(
                0,
                24
            ) else holder.periods
            val tempGraph = TemperatureGraph(this.context, periods, holder.chart)
            tempGraph.animateTime = 500
            tempGraph.build()

            holder.scrollView.scrollTo(0, 0)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == forecastData.size) R.layout.item_sevenday_button else R.layout.item_sevenday
    }
}

class SevenDayViewHolder(itemView: View, val binding: ItemSevendayBinding) :
    RecyclerView.ViewHolder(itemView) {
    val expandableLayout = itemView.rlExpandableLayout
    val chart = itemView.dayHourlyChart
    val scrollView = itemView.svDayChart
    lateinit var periods: List<Period>

    fun bind(dayForecast: DayForecast) {
        val day = dayForecast.high!!.name
        val shortForecast = dayForecast.high!!.shortForecast
        val highTemp = dayForecast.high!!.temperature
        val lowTemp = dayForecast.low!!.temperature
        val iconUrl = dayForecast.high!!.icon?.replaceAfter("=", "large")
        val tempUnit = dayForecast.tempUnit!!
        periods = dayForecast.hourly!!

        binding.tvDay.text = day
        itemView.tvShortForecast.text = shortForecast
        itemView.tvHighTemp.text = "$highTemp\u00B0$tempUnit"
        itemView.tvLowTemp.text = "$lowTemp\u00B0$tempUnit"

        itemView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)

        Picasso.get().load(iconUrl)
            .fit()
            .into(itemView.ivForecastIcon)
    }
}