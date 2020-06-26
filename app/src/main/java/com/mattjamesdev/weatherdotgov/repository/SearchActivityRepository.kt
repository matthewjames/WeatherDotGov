package com.mattjamesdev.weatherdotgov.repository

import android.app.Application
import androidx.lifecycle.MutableLiveData

class SearchActivityRepository(application: Application) {
    val isLoading = MutableLiveData<Boolean>()

    fun changeState(){
        isLoading.value = !(isLoading.value != null && isLoading.value!!)
    }
}