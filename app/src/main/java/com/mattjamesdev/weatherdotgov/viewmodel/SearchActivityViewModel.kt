package com.mattjamesdev.weatherdotgov.viewmodel

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mattjamesdev.weatherdotgov.network.model.DayForecast
import com.mattjamesdev.weatherdotgov.network.model.ForecastData
import com.mattjamesdev.weatherdotgov.network.model.Location
import com.mattjamesdev.weatherdotgov.network.model.Period
import com.mattjamesdev.weatherdotgov.repository.SearchActivityRepository
import kotlinx.coroutines.*
import retrofit2.HttpException
import kotlin.ClassCastException

class SearchActivityViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "SearchActivityVM"
    private val repository = SearchActivityRepository()
    val isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val dailyForecastData: MutableLiveData<MutableList<DayForecast>> = MutableLiveData()
    val hourlyForecastData: MutableLiveData<ForecastData> = MutableLiveData()


    fun getForecastData(coords : String){
        GlobalScope.launch(Dispatchers.Main){
            isLoading.value = true

            try {
                val locationData: Location = withContext(Dispatchers.IO){ repository.getLocationProperties(coords) }
                Log.d(TAG, "Location data: $locationData")

                val wfo = locationData.properties.gridId
                val x = locationData.properties.gridX
                val y = locationData.properties.gridY

                Log.d(TAG, "Fetching hourly forecast data")
                val hourlyData: Deferred<ForecastData> = GlobalScope.async(Dispatchers.IO){ repository.getHourlyForecastData(wfo, x, y) }
//            Log.d(TAG, "hourlyForecastData: ${hourlyData.await()}")
                hourlyForecastData.value = hourlyData.await()

                Log.d(TAG, "Fetching seven day forecast data")
                val sevenDayData: Deferred<ForecastData> = GlobalScope.async(Dispatchers.IO){ repository.getSevenDayForecastData(wfo, x, y) }
//            Log.d(TAG, "sevenDayForecastData: ${sevenDayData.await()}")

                dailyForecastData.value = combineLatestData(hourlyData.await(), sevenDayData.await())
//            Log.d(TAG, "dailyForecastData: $dailyForecastData")
            } catch (e: Exception) {
                FirebaseCrashlytics.getInstance().log("$TAG Exception thrown: $e")
                TODO("Add Toast/Snackbar message to user: 'Error loading forecast. Try Again.' with retry button")
            }

            isLoading.value = false
        }
    }

    // parses the hourly and seven day forecast data into MutableList<DayForecast>
    private fun combineLatestData(
        hourlyForecastData: ForecastData,
        sevenDayForecastData: ForecastData
    ): MutableList<DayForecast> {
        Log.d(TAG, "combineLatestData() entered")

        // parse hourly data into DayForecast objects and add to dailyData
        var hourlyPeriods: List<Period> = hourlyForecastData.properties.periods
        var sevenDayPeriods: List<Period> = sevenDayForecastData.properties.periods

        // initialize list of empty dayForecasts
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

                if(dayForecast.date == hourlyPeriods[j].startTime.substring(0,10)){
                    dayHourlyPeriodList.add(hourlyPeriods[j])
                } else {
                    dayForecast.hourly = dayHourlyPeriodList
                    hourlyPeriods = hourlyPeriods.slice(j until hourlyPeriods.size) as MutableList<Period>
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
                    sevenDayPeriods = sevenDayPeriods.slice(2 until sevenDayPeriods.size)
                } catch (e: ClassCastException) {
                    print(e.printStackTrace())
                }
            } else {
                dayForecast.hourly!![0].name = sevenDayPeriods[0].name

                dayForecast.high = dayForecast.hourly!![0]
                dayForecast.low = sevenDayPeriods[0]
                try {
                    sevenDayPeriods = sevenDayPeriods.slice(1 until sevenDayPeriods.size)
                } catch (e: ClassCastException) {
                    print(e.printStackTrace())
                }
            }

            // initialize tempUnit

            dayForecast.tempUnit = dayHourlyPeriodList[0].temperatureUnit
        }

//        Log.d(TAG,"combineLatestData() returning $dayForecastList")
        return dayForecastList
    }
}