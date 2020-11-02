package com.mattjamesdev.weatherdotgov.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mattjamesdev.weatherdotgov.model.*
import com.mattjamesdev.weatherdotgov.repository.SearchActivityRepository
import kotlinx.coroutines.*

class SearchActivityViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "SearchActivityVM"
    private val repository = SearchActivityRepository()
    val isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val gridpointData: MutableLiveData<GridpointData> = MutableLiveData()
    val dailyForecastData: MutableLiveData<MutableList<DayForecast>> = MutableLiveData()
    val hourlyForecastData: MutableLiveData<ForecastData> = MutableLiveData()
    val hasAlert: MutableLiveData<Boolean> = MutableLiveData(false)
    val errorMessage: MutableLiveData<String> = MutableLiveData()
    val cityState: MutableLiveData<String> = MutableLiveData()
    lateinit var alertData: AlertData

    fun getForecastData(latitude: Double, longtitude: Double){
        GlobalScope.launch(Dispatchers.Main){
            isLoading.value = true

            try {
                val forecastArea: ForecastArea = withContext(Dispatchers.IO){ repository.getForecastArea(latitude, longtitude) }
//                Log.d(TAG, "Location data: $forecastArea")

                val wfo = forecastArea.properties.gridId
                val x = forecastArea.properties.gridX
                val y = forecastArea.properties.gridY
                val zoneId = forecastArea.properties.forecastZone.substringAfter("https://api.weather.gov/zones/forecast/")

                cityState.value = "${forecastArea.properties.relativeLocation.properties.city}, ${forecastArea.properties.relativeLocation.properties.state}"

                // Get gridpoint data
                Log.d(TAG, "Fetching gridpoint data...")
                val currentGridpointData: Deferred<GridpointData> = GlobalScope.async (Dispatchers.IO){ repository.getGridpointData(wfo, x, y) }
                Log.d(TAG, "Gridpoint data: ${currentGridpointData.await()}")
                gridpointData.value = currentGridpointData.await()

                // Get hourly forecast
                Log.d(TAG, "Fetching hourly forecast data...")
                val currentHourlyData: Deferred<ForecastData> = GlobalScope.async(Dispatchers.IO){ repository.getHourlyForecastData(wfo, x, y) }
//            Log.d(TAG, "hourlyForecastData: ${hourlyData.await()}")
                hourlyForecastData.value = currentHourlyData.await()

                // Get Seven Day forecast
                Log.d(TAG, "Fetching seven day forecast data...")
                val currentSevenDayData: Deferred<ForecastData> = GlobalScope.async(Dispatchers.IO){ repository.getSevenDayForecastData(wfo, x, y) }
//            Log.d(TAG, "sevenDayForecastData: ${sevenDayData.await()}")

                // Get alert data
                Log.d(TAG, "Fetching alert data")
                val currentAlertData: Deferred<AlertData> = GlobalScope.async (Dispatchers.IO){ repository.getAlertData(zoneId) }
                Log.d(TAG, "alertData: ${currentAlertData.await()}")
                alertData = currentAlertData.await()
                hasAlert.value = !alertData.features.isEmpty()

                dailyForecastData.value = combineLatestData(currentHourlyData.await(), currentSevenDayData.await())
//            Log.d(TAG, "dailyForecastData: $dailyForecastData")
            } catch (e: Exception) {
                Log.d(TAG, "Exception thrown: $e")
                FirebaseCrashlytics.getInstance().log("$TAG: Exception thrown: $e")
                FirebaseCrashlytics.getInstance().sendUnsentReports()
                errorMessage.value = "Error loading forecast data. Try again."
            }

            isLoading.value = false
        }
    }

    // parses the hourly and seven day forecast data into MutableList<DayForecast>
    private fun combineLatestData(hourlyForecastData: ForecastData, sevenDayForecastData: ForecastData): MutableList<DayForecast> {
        Log.d(TAG, "Combining hourly and daily forecast data...")

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
                    hourlyPeriods = hourlyPeriods.slice(j until hourlyPeriods.size) as MutableList<Period>
                    break
                }
            }

            dayForecast.hourly = dayHourlyPeriodList

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