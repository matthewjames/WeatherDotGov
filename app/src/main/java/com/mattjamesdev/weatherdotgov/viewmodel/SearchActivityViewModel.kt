package com.mattjamesdev.weatherdotgov.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.mattjamesdev.weatherdotgov.network.model.DayForecast
import com.mattjamesdev.weatherdotgov.network.model.ForecastData
import com.mattjamesdev.weatherdotgov.network.model.Period
import com.mattjamesdev.weatherdotgov.repository.SearchActivityRepository

class SearchActivityViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = SearchActivityRepository(application)
    val isLoading: LiveData<Boolean>
    val dailyForecastData: LiveData<MutableList<DayForecast>>

    init {
        this.isLoading = repository.isLoading
        this.dailyForecastData = parseDailyForecastData()
    }

    fun changeState(){
        repository.changeState()
    }

    fun getLocationProperties(coords : String){
        repository.getLocationProperties(coords)
    }

    private fun parseDailyForecastData(): LiveData<MutableList<DayForecast>> {
        val hourlyForecastData: LiveData<ForecastData> = repository.hourlyForecastData
        val sevenDayForecastData: LiveData<ForecastData> = repository.sevenDayForecastData

        val combinedData = MediatorLiveData<MutableList<DayForecast>>()

        combinedData.addSource(hourlyForecastData) { value ->
            combinedData.value = combineLatestData(hourlyForecastData, sevenDayForecastData)
        }

        combinedData.addSource(sevenDayForecastData) { value ->
            combinedData.value = combineLatestData(hourlyForecastData, sevenDayForecastData)
        }

        return combinedData
    }


    // parses the hourly and seven day forecast data into MutableList<DayForecast>
    private fun combineLatestData(
        hourlyForecastData: LiveData<ForecastData>,
        sevenDayForecastData: LiveData<ForecastData>
    ): MutableList<DayForecast> {

        // parse hourly data into DayForecast objects and add to dailyData
        val hourlyData = hourlyForecastData.value!!
        val sevenDayData = sevenDayForecastData.value!!
        var hourlyPeriods: MutableList<Period> = hourlyData.properties.periods
        var sevenDayPeriods: MutableList<Period> = sevenDayData.properties.periods

        val dayForecastList = MutableList<DayForecast>(14){index -> DayForecast()}

        // initialize each DayForecast
        for(dayForecast in dayForecastList) {

            // initialize date
            val date = hourlyPeriods[0].startTime.substring(0..9)
            dayForecast.date = date

            // initialize hourly

            // iterate through hourly data and populate new list of hourly periods with same date
            val dayHourlyPeriodList = mutableListOf<Period>()
            for(j in 0 until hourlyPeriods.size - 1){
                /**
                 * if DayForecast.date is the same as current period date:
                 *      add to list of periods
                 * else:
                 *      export list of periods to DayForecast object
                 *      slice list from j -> end of list
                 */

                if(dayForecast.date == hourlyPeriods[j].startTime.substring(0..9)){
                    dayHourlyPeriodList.add(hourlyPeriods[j])
                } else {
                    dayForecast.hourly = dayHourlyPeriodList
                    hourlyPeriods = hourlyPeriods.slice(j..hourlyPeriods.size) as MutableList<Period>
                    break
                }
            }

            // initialize high and low

            // iterate through sevenDay data
            /**
             * if isDayTime:
             *      assign high to first element in sevenDayList
             *      assign low to second element in sevenDayList
             *      slice sevenDayList from 2 -> end of list
             *  else:
             *      assign high to first element in DayForecast.hourly
             *      assing low to first element in sevenDayList
             *      slice sevenDayList from 1 -> end of list
             */

            if(sevenDayPeriods[0].isDaytime){
                dayForecast.high = sevenDayPeriods[0]
                dayForecast.low = sevenDayPeriods[1]
                sevenDayPeriods = sevenDayPeriods.slice(2..sevenDayPeriods.size) as MutableList<Period>
            } else {
                dayForecast.high = dayForecast.hourly!!.get(0)
                dayForecast.low = sevenDayPeriods[0]
                sevenDayPeriods = sevenDayPeriods.slice(1..sevenDayPeriods.size) as MutableList<Period>
            }
        }
        return dayForecastList
    }

    private fun parseHourlyData(dailyData: MutableList<DayForecast>){

    }
}