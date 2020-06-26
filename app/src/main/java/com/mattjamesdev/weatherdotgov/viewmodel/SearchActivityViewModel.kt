package com.mattjamesdev.weatherdotgov.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.mattjamesdev.weatherdotgov.repository.SearchActivityRepository

class SearchActivityViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = SearchActivityRepository(application)
    val isLoading : LiveData<Boolean>

    init {
        this.isLoading = repository.isLoading
    }

    fun changeState(){
        repository.changeState()
    }
}