package com.mattjamesdev.weatherdotgov.viewmodel

import android.app.Application
import android.nfc.Tag
import android.util.Log
import androidx.lifecycle.*
import com.mattjamesdev.weatherdotgov.network.model.DayForecast
import com.mattjamesdev.weatherdotgov.network.model.ForecastData
import com.mattjamesdev.weatherdotgov.network.model.Location
import com.mattjamesdev.weatherdotgov.network.model.Period
import com.mattjamesdev.weatherdotgov.repository.SearchActivityRepository
import kotlinx.coroutines.*
import kotlin.ClassCastException

class SearchActivityViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "SearchActivityVM"
    private val repository = SearchActivityRepository(application)
    val isLoading: LiveData<Boolean>
    val dailyForecastData: MutableLiveData<MutableList<DayForecast>> = MutableLiveData()

    init {
        this.isLoading = repository.isLoading
    }

    fun changeState(){
        repository.changeState()
    }

    fun getForecastData(coords : String){

        GlobalScope.launch(Dispatchers.Main){
            val locationData: Location = withContext(Dispatchers.IO){ repository.getLocationProperties(coords) }

            Log.d(TAG, "Location data: $locationData")

            val wfo = locationData.properties.gridId
            val x = locationData.properties.gridX
            val y = locationData.properties.gridY

            val hourlyForecastData: Deferred<ForecastData> = GlobalScope.async(Dispatchers.IO){ repository.getHourlyForecastData(wfo, x, y) }

            Log.d(TAG, "hourlyForecastData: ${hourlyForecastData.await()}")

            val sevenDayForecastData: Deferred<ForecastData> = GlobalScope.async(Dispatchers.IO){ repository.getSevenDayForecastData(wfo, x, y) }

            Log.d(TAG, "sevenDayForecastData: ${sevenDayForecastData.await()}")

            dailyForecastData.value = combineLatestData(hourlyForecastData.await(), sevenDayForecastData.await())

            Log.d(TAG, "dailyForecastData: $dailyForecastData")
        }
    }

    suspend fun fetchLocationData(coords: String): LiveData<Location>{
        return GlobalScope.async(Dispatchers.IO){liveData(Dispatchers.IO){
            val data = repository.getLocationProperties(coords)
            emit(data)
        }}.await()
    }

    // parses the hourly and seven day forecast data into MutableList<DayForecast>
    private fun combineLatestData(
        hourlyForecastData: ForecastData,
        sevenDayForecastData: ForecastData
    ): MutableList<DayForecast> {
        Log.d(TAG, "combineLatestData() entered")

        // parse hourly data into DayForecast objects and add to dailyData
        val hourlyData = hourlyForecastData
        val sevenDayData = sevenDayForecastData
        var hourlyPeriods: MutableList<Period> = hourlyData.properties.periods
        var sevenDayPeriods: MutableList<Period> = sevenDayData.properties.periods

        val dayForecastList = MutableList(7){index -> DayForecast()}

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
                    hourlyPeriods = hourlyPeriods.slice(j..hourlyPeriods.size-1) as MutableList<Period>
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
             *      assign low to first element in sevenDayList
             *      slice sevenDayList from 1 -> end of list
             */

            if(sevenDayPeriods[0].isDaytime){
                dayForecast.high = sevenDayPeriods[0]
                dayForecast.low = sevenDayPeriods[1]
                try {
                    sevenDayPeriods = sevenDayPeriods.slice(2..sevenDayPeriods.size-1) as MutableList<Period>
                } catch (e: ClassCastException) {
                    print(e.printStackTrace())
                }
            } else {
                dayForecast.high = dayForecast.hourly!![0]
                dayForecast.low = sevenDayPeriods[0]
                try {
                    sevenDayPeriods = sevenDayPeriods.slice(1..sevenDayPeriods.size-1) as MutableList<Period>
                } catch (e: ClassCastException) {
                    print(e.printStackTrace())
                }
            }
        }

        return dayForecastList
    }
}