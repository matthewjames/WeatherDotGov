package com.mattjamesdev.weatherdotgov.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.mattjamesdev.weatherdotgov.network.model.ForecastData
import com.mattjamesdev.weatherdotgov.repository.SearchActivityRepository

class SearchActivityViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = SearchActivityRepository(application)
    val isLoading: LiveData<Boolean>
    val hourlyForecastData: LiveData<ForecastData>
    val sevenDayForecastData: LiveData<ForecastData>

    init {
        this.isLoading = repository.isLoading
        this.hourlyForecastData = repository.hourlyForecastData
        this.sevenDayForecastData = repository.sevenDayForecastData
    }

    fun changeState(){
        repository.changeState()
    }

    fun getLocationProperties(coords : String){
        repository.getLocationProperties(coords)
    }
}