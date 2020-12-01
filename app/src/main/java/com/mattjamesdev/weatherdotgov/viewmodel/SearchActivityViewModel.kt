package com.mattjamesdev.weatherdotgov.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.mattjamesdev.weatherdotgov.model.*
import com.mattjamesdev.weatherdotgov.repository.SearchActivityRepository
import kotlinx.coroutines.*
import retrofit2.Response
import java.lang.Exception
import kotlin.properties.Delegates
import kotlin.system.measureTimeMillis

class SearchActivityViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "SearchActivityVM"
    private val repository = SearchActivityRepository()
    val isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val gridpointData: MutableLiveData<GridpointData> = MutableLiveData()
    val dailyForecastData: MutableLiveData<MutableList<DayForecast>> = MutableLiveData()
    val hourlyForecastData: MutableLiveData<ForecastData> = MutableLiveData()
    val alertData: MutableLiveData<AlertData> = MutableLiveData()
    val errorMessage: MutableLiveData<String> = MutableLiveData()
    val cityState: MutableLiveData<String> = MutableLiveData()
    val pointForecastLatLong: MutableLiveData<String> = MutableLiveData()
    var mLatitude: Double = 0.0
    var mLongitude: Double = 0.0
    val timeElapsed = measureTimeMillis { getForecastData() }
    private lateinit var wfo: String
    private var x by Delegates.notNull<Int>()
    private var y by Delegates.notNull<Int>()
    private lateinit var zoneId: String

    fun getForecastData(){
        lateinit var forecastArea: Response<ForecastArea>
        lateinit var forecastAreaProperties: ForecastAreaProperties
        lateinit var currentGridpointData: GridpointData
        lateinit var currentHourlyData: ForecastData
        lateinit var currentSevenDayData: ForecastData
        lateinit var currentAlertData: AlertData

        isLoading.value = true

        var testString = "This is a test string"

        try {
            viewModelScope.launch(Dispatchers.IO){
                runBlocking {
                    forecastArea = repository.getForecastArea(mLatitude, mLongitude)
                    Log.d(TAG, "ForecastArea data fetch successful: ${forecastArea.isSuccessful}")
                }

                if(forecastArea.isSuccessful) {
                    forecastAreaProperties = forecastArea.body()!!.properties

                    wfo = forecastAreaProperties.gridId
                    x = forecastAreaProperties.gridX
                    y = forecastAreaProperties.gridY
                    zoneId =
                        forecastAreaProperties.forecastZone.substringAfter("https://api.weather.gov/zones/forecast/")
                    mLatitude = forecastAreaProperties.relativeLocation.geometry.coordinates[1]
                    mLongitude = forecastAreaProperties.relativeLocation.geometry.coordinates[0]

                    currentGridpointData = async {
                        Log.d(TAG, "Fetching gridpoint data...")
                        repository.getGridpointData(wfo, x, y)
                    }.await()

                    currentHourlyData = async {
                        Log.d(TAG, "Fetching hourly forecast data...")
                        repository.getHourlyForecastData(wfo, x, y)
                    }.await()

                    currentSevenDayData = async {
                        Log.d(TAG, "Fetching seven day forecast data...")
                        repository.getSevenDayForecastData(wfo, x, y)
                    }.await()

                    currentAlertData = async {
                        Log.d(TAG, "Fetching alert data")
                        repository.getAlertData(zoneId)
                    }.await()

                    launch(Dispatchers.Main) {
                        cityState.value =
                            "${forecastAreaProperties.relativeLocation.properties.city}, ${forecastAreaProperties.relativeLocation.properties.state}"
                        pointForecastLatLong.value = "${mLatitude}°N ${mLongitude * -1}°W"
                        gridpointData.value = currentGridpointData
                        hourlyForecastData.value = currentHourlyData
                        alertData.value = currentAlertData
                        dailyForecastData.value = withContext(Dispatchers.Default){ combineLatestData(currentHourlyData, currentSevenDayData) }
                        isLoading.value = false
                    }
                }
            }
        } catch (e: Exception) {
            Log.d(TAG, "Exception thrown: $e")
            FirebaseCrashlytics.getInstance().log("$TAG: Exception thrown: $e")
            FirebaseCrashlytics.getInstance().sendUnsentReports()
            isLoading.value = false
            errorMessage.value = "Error loading forecast data. Try again."
        }





//        viewModelScope.launch(Dispatchers.Main){
//
//            try {
//                val forecastArea: ForecastArea = withContext(Dispatchers.IO) {
//                    repository.getForecastArea(mLatitude, mLongitude)
//                }
//                Log.d(TAG, "Location data: $forecastArea")
//
//                wfo = forecastArea.properties.gridId
//                x = forecastArea.properties.gridX
//                y = forecastArea.properties.gridY
//                zoneId =
//                    forecastArea.properties.forecastZone.substringAfter("https://api.weather.gov/zones/forecast/")
//
//                cityState.value =
//                    "${forecastArea.properties.relativeLocation.properties.city}, ${forecastArea.properties.relativeLocation.properties.state}"
//                mLatitude = forecastArea.properties.relativeLocation.geometry.coordinates[1]
//                mLongitude = forecastArea.properties.relativeLocation.geometry.coordinates[0]
//                pointForecastLatLong.value = "${mLatitude}°N ${mLongitude * -1}°W"
//            } catch (e: Exception) {
//                Log.d(TAG, "Exception thrown while fetching forecast area data: $e")
//                FirebaseCrashlytics.getInstance().log("$TAG: Exception thrown while fetching forecast area data: $e")
//                FirebaseCrashlytics.getInstance().sendUnsentReports()
//                errorMessage.value = "Error loading forecast data. Try again."
//            }
//
//            // Get gridpoint data
//            try {
//                val currentGridpointData = viewModelScope.async (Dispatchers.IO){
//                    Log.d(TAG, "Fetching gridpoint data...")
//                    repository.getGridpointData(wfo, x, y)
//                }.await()
////                Log.d(TAG, "Gridpoint data: ${currentGridpointData}")
//                gridpointData.value = currentGridpointData
//            } catch (e: Exception) {
//                Log.d(TAG, "Exception thrown while fetching gridpoint data: $e")
//                FirebaseCrashlytics.getInstance().log("$TAG: Exception thrown while fetching gridpoint data: $e")
//                FirebaseCrashlytics.getInstance().sendUnsentReports()
//                errorMessage.value = "Error loading forecast data. Try again."
//            }
//
//            try {
//                // Get hourly forecast
//                val currentHourlyData: Deferred<ForecastData> = viewModelScope.async(Dispatchers.IO){
//                    Log.d(TAG, "Fetching hourly forecast data...")
//                    repository.getHourlyForecastData(wfo, x, y)
//                }
////            Log.d(TAG, "hourlyForecastData: ${hourlyData.await()}")
//                hourlyForecastData.value = currentHourlyData.await()
//
//                // Get Seven Day forecast
//                val currentSevenDayData: Deferred<ForecastData> = viewModelScope.async(Dispatchers.IO){
//                    Log.d(TAG, "Fetching seven day forecast data...")
//                    repository.getSevenDayForecastData(wfo, x, y)
//                }
////            Log.d(TAG, "sevenDayForecastData: ${sevenDayData.await()}")
//
//                // Get alert data
//
//                val currentAlertData: Deferred<AlertData> = viewModelScope.async (Dispatchers.IO){
//                    Log.d(TAG, "Fetching alert data")
//                    repository.getAlertData(zoneId)
//                }
////                Log.d(TAG, "alertData: ${currentAlertData.await()}")
//                alertData.value = currentAlertData.await()
//
//                dailyForecastData.value = withContext(Dispatchers.Default) { combineLatestData(currentHourlyData.await(), currentSevenDayData.await()) }
////            Log.d(TAG, "dailyForecastData: $dailyForecastData")
//            } catch (e: Exception) {
//                Log.d(TAG, "Exception thrown: $e")
//                FirebaseCrashlytics.getInstance().log("$TAG: Exception thrown: $e")
//                FirebaseCrashlytics.getInstance().sendUnsentReports()
//                errorMessage.value = "Error loading forecast data. Try again."
//            }
//
//        }
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