package com.mattjamesdev.weatherdotgov.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mattjamesdev.weatherdotgov.data.model.Period
import com.mattjamesdev.weatherdotgov.data.model.alerts.AlertsResponse
import com.mattjamesdev.weatherdotgov.data.model.gridpoint.GridpointResponse
import com.mattjamesdev.weatherdotgov.data.model.hourly.HourlyForecastResponse
import com.mattjamesdev.weatherdotgov.data.model.sevenday.SevenDayForecastResponse
import com.mattjamesdev.weatherdotgov.domain.StateData
import com.mattjamesdev.weatherdotgov.domain.model.DayForecast
import com.mattjamesdev.weatherdotgov.domain.model.ForecastAreaV2
import com.mattjamesdev.weatherdotgov.domain.model.LatLong
import com.mattjamesdev.weatherdotgov.repository.SearchActivityRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchActivityViewModel(application: Application) : AndroidViewModel(application) {
    private val _location = MutableStateFlow(LatLong.EMPTY)
    val location = _location.asStateFlow()

    private val _errorMessage = MutableStateFlow("")
    val errorMessage = _errorMessage.asStateFlow()

    // legacy vals/vars
    private val TAG = "SearchActivityVM"
    private val repository = SearchActivityRepository()
    val isLoading: MutableLiveData<Boolean> = MutableLiveData(false)
    val gridpointData: MutableLiveData<GridpointResponse> = MutableLiveData()
    val dailyForecastData: MutableLiveData<MutableList<DayForecast>> = MutableLiveData()
    val hourlyForecastData: MutableLiveData<HourlyForecastResponse> = MutableLiveData()
    val alertData: MutableLiveData<AlertsResponse> = MutableLiveData()
//    val errorMessage: MutableLiveData<String> = MutableLiveData()
    val cityState: MutableLiveData<String> = MutableLiveData()
    val pointForecastLatLong: MutableLiveData<String> = MutableLiveData()
    var mLatitude: Double = 0.0
    var mLongitude: Double = 0.0


    fun setLocation(latLong: LatLong) {
        Log.d(TAG, "New location set: lat=${latLong.lat}, long=${latLong.long}")
        _location.value = latLong
    }

    fun fetchForecastAreaV2(latLong: LatLong){
        viewModelScope.launch {
            repository.getForecastAreaV2(latLong.lat, latLong.long).collect { forecastAreaStateData ->
                getForecastDataV2(forecastAreaStateData)
            }
        }
    }

    private fun getForecastDataV2(forecastAreaStateData: StateData<ForecastAreaV2?>){
        when(forecastAreaStateData){
            is StateData.Ready -> {
                val forecastArea = forecastAreaStateData.data
                getGridpointData(forecastArea)
                hourlyForecastData(forecastArea)
                alertData(forecastArea)
                isLoading.value = false
            }
            is StateData.Loading -> {
                isLoading.value = true
            }
            is StateData.Error -> {
                showError(forecastAreaStateData.message)
                isLoading.value = false
            }
        }
    }

    private fun getGridpointData(forecastArea: ForecastAreaV2?){
        forecastArea?.let { forecastArea ->
            viewModelScope.launch {
                gridpointData.value = repository.getGridpointData(forecastArea)
            }
        }
    }

    private fun hourlyForecastData(forecastArea: ForecastAreaV2?){
        forecastArea?.let { forecastArea ->
            viewModelScope.launch {
                hourlyForecastData.value = repository.getHourlyForecastData(forecastArea)
            }
        }
    }

    private fun alertData(forecastArea: ForecastAreaV2?){
        forecastArea?.let { forecastArea ->
            viewModelScope.launch {
                alertData.value = repository.getAlertData(forecastArea)
            }
        }
    }

    private fun showError(message: String){
        viewModelScope.launch {
            _errorMessage.value = message
        }
    }

    // legacy functions
//    fun fetchForecastAreaV2(){
//        GlobalScope.launch(Dispatchers.Main){
//            isLoading.value = true
//
//            try {
//                val forecastArea: ForecastArea = withContext(Dispatchers.IO){ repository.getForecastArea(mLatitude, mLongitude) }
////                Log.d(TAG, "Location data: $forecastArea")
//
//                val wfo = forecastArea.properties.gridId
//                val x = forecastArea.properties.gridX
//                val y = forecastArea.properties.gridY
//                val zoneId = forecastArea.properties.forecastZone.substringAfter("https://api.weather.gov/zones/forecast/")
//
//                cityState.value = "${forecastArea.properties.relativeLocation.properties.city}, ${forecastArea.properties.relativeLocation.properties.state}"
//                mLatitude = forecastArea.properties.relativeLocation.geometry.coordinates[1]
//                mLongitude = forecastArea.properties.relativeLocation.geometry.coordinates[0]
//                pointForecastLatLong.value = "${mLatitude}°N ${mLongitude*-1}°W"
//
//                // Get gridpoint data
//                Log.d(TAG, "Fetching gridpoint data...")
//                val currentGridpointData = GlobalScope.async (Dispatchers.IO){ repository.getGridpointData(wfo, x, y) }.await()
//                Log.d(TAG, "Gridpoint data: ${currentGridpointData}")
//                gridpointData.value = currentGridpointData
//
//                // Get hourly forecast
//                Log.d(TAG, "Fetching hourly forecast data...")
//                val currentHourlyData: Deferred<ForecastData> = GlobalScope.async(Dispatchers.IO){ repository.getHourlyForecastData(wfo, x, y) }
////            Log.d(TAG, "hourlyForecastData: ${hourlyData.await()}")
//                hourlyForecastData.value = currentHourlyData.await()
//
//                // Get Seven Day forecast
//                Log.d(TAG, "Fetching seven day forecast data...")
//                val currentSevenDayData: Deferred<ForecastData> = GlobalScope.async(Dispatchers.IO){ repository.getSevenDayForecastData(wfo, x, y) }
////            Log.d(TAG, "sevenDayForecastData: ${sevenDayData.await()}")
//
//                // Get alert data
//                Log.d(TAG, "Fetching alert data")
//                val currentAlertData: Deferred<AlertData> = GlobalScope.async (Dispatchers.IO){ repository.getAlertData(zoneId) }
//                Log.d(TAG, "alertData: ${currentAlertData.await()}")
//                alertData.value = currentAlertData.await()
//
//                dailyForecastData.value = combineLatestData(currentHourlyData.await(), currentSevenDayData.await())
////            Log.d(TAG, "dailyForecastData: $dailyForecastData")
//            } catch (e: Exception) {
//                Log.d(TAG, "Exception thrown: $e")
//                FirebaseCrashlytics.getInstance().log("$TAG: Exception thrown: $e")
//                FirebaseCrashlytics.getInstance().sendUnsentReports()
//                errorMessage.value = "Error loading forecast data. Try again."
//            }
//
//            isLoading.value = false
//        }
//    }

    // parses the hourly and seven day forecast data into MutableList<DayForecast>
    private fun combineLatestData(hourlyForecastData: HourlyForecastResponse, sevenDayForecastData: SevenDayForecastResponse): MutableList<DayForecast> {
        Log.d(TAG, "Combining hourly and daily forecast data...")

        // parse hourly data into DayForecast objects and add to dailyData
        var hourlyPeriods: List<Period?>? = hourlyForecastData.properties?.periods
        var sevenDayPeriods: List<Period?>? = sevenDayForecastData.properties?.periods

        // initialize list of empty dayForecasts
        val dayForecastList = MutableList(7){index -> DayForecast() }

        // initialize each DayForecast
        for(dayForecast in dayForecastList) {

            // initialize date
            val date = hourlyPeriods?.get(0)?.startTime?.substring(0..9)
            dayForecast.date = date

            // initialize hourly

            // iterate through hourly data and populate new list of hourly periods with same date
            val dayHourlyPeriodList = mutableListOf<Period>()
            for(j in 0 until (hourlyPeriods?.size?.minus(1) ?: 0)){
                /**
                 * if DayForecast.date is the same as current period date:
                 *      add to list of periods
                 * else:
                 *      export list of periods to DayForecast object
                 *      slice list from j -> end of list
                 */

                if(dayForecast.date == hourlyPeriods?.get(j)?.startTime?.substring(0,10)){
                    hourlyPeriods?.get(j)?.let { dayHourlyPeriodList.add(it) }
                } else {
                    hourlyPeriods = hourlyPeriods?.slice(j until hourlyPeriods.size) as MutableList<Period?>?
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

            if(sevenDayPeriods?.get(0)?.isDaytime == true){
                dayForecast.high = sevenDayPeriods[0]
                dayForecast.low = sevenDayPeriods[1]
                try {
                    sevenDayPeriods = sevenDayPeriods.slice(2 until sevenDayPeriods.size)
                } catch (e: ClassCastException) {
                    print(e.printStackTrace())
                }
            } else {
                dayForecast.hourly!![0].name = sevenDayPeriods?.get(0)?.name

                dayForecast.high = dayForecast.hourly!![0]
                dayForecast.low = sevenDayPeriods?.get(0)
                try {
                    sevenDayPeriods = sevenDayPeriods?.slice(1 until sevenDayPeriods.size)
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