package com.mattjamesdev.weatherdotgov

object Keys {

    init {
        System.loadLibrary("native-lib")
    }

    external fun apiKey(): String
}