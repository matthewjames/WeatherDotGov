package com.mattjamesdev.weatherdotgov.utils

import android.content.Context
import android.graphics.Typeface
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.mattjamesdev.weatherdotgov.R
import com.mattjamesdev.weatherdotgov.model.Period
import java.text.DecimalFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TemperatureGraph(val context: Context, val periods: List<Period>, val chart: LineChart) {
    private val labels = createXAxisLabels()
    private val dataSet = createLineChartDataSet()
    var animateTime = 1500

    init {
        chart.data = LineData(dataSet)
        chart.notifyDataSetChanged()
    }

    private fun createXAxisLabels() : List<String> {
        val labels = mutableListOf<String>()
        val fromFormat = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        val toFormat = DateTimeFormatter.ofPattern("h a")

        for(i in periods.indices){
            val label = LocalDateTime.parse(periods[i].startTime, fromFormat).format(toFormat)
//            Log.d(TAG, "Label $label created from ${periods[i].startTime}")

            labels.add(
                if(i == 0 || i == (periods.size-1)){
                    ""
                } else {
                    label
                }
            )
        }

        return labels
    }

    private fun createLineChartDataSet() : LineDataSet {
        val entries = ArrayList<Entry>()

        for(i in periods.indices){
            // parse hourly forecast data into entries for data set
            // entries parsed as (hour, temperature)

            val temperature = periods[i].temperature.toFloat()
            entries.add(Entry(i.toFloat(), temperature))
        }

        val lineDataSet = LineDataSet(entries, "Temperature")
        lineDataSet.isHighlightEnabled = false
        lineDataSet.setDrawFilled(true)
        lineDataSet.fillDrawable = ContextCompat.getDrawable(context, R.drawable.gradient_temp_chart)

        lineDataSet.setDrawValues(true)
        lineDataSet.valueTextSize = 16f
        lineDataSet.valueTextColor = ContextCompat.getColor(context, R.color.textChartDataPointLight)
        lineDataSet.valueTypeface = Typeface.DEFAULT_BOLD

        lineDataSet.setDrawCircles(true)
        lineDataSet.setDrawCircleHole(false)
        lineDataSet.setCircleColor(ContextCompat.getColor(context, R.color.transparent))
        lineDataSet.circleRadius = 6f

        lineDataSet.enableDashedLine(0f,1f,0f)
        lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
        lineDataSet.cubicIntensity = 0.2f

        lineDataSet.valueFormatter = MyValueFormatter()

        return lineDataSet
    }

    fun build(){
        chart.axisRight.isEnabled = false

        chart.axisLeft.isEnabled = false
        chart.axisLeft.axisMinimum = chart.yMin - 10f

        chart.xAxis.setDrawGridLines(false)
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.xAxis.granularity = 1f
        chart.xAxis.setLabelCount(labels.size, true)
        chart.xAxis.textSize = 14f
        chart.xAxis.textColor = ContextCompat.getColor(context, R.color.textChartXAxisDark)
        chart.xAxis.typeface = Typeface.DEFAULT_BOLD
        chart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        chart.xAxis.axisLineColor = ContextCompat.getColor(context, R.color.textInfoPanelLight)
        chart.xAxis.yOffset = 10f

        chart.setNoDataText("No temperature values!")
        chart.legend.isEnabled = false
        chart.description.isEnabled = false
        chart.setTouchEnabled(false)
        chart.setViewPortOffsets(0f,100.0f,0f,100.0f)
        chart.animateY(animateTime)

        chart.postInvalidate()
    }
}

class MyValueFormatter : ValueFormatter() {
    private val format = DecimalFormat("###°")

    override fun getPointLabel(entry: Entry?): String {
        return if(entry?.x == 0f || entry?.x == 23f){
            ""
        } else {
            format.format(entry?.y)
        }
    }
}