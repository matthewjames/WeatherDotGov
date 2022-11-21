package com.mattjamesdev.weatherdotgov.domain

sealed class StateData<T>(
    val state: State = State.Inactive,
    val data: T? = null,
    val message: String = "",
) {
    class Ready<T>(data: T): StateData<T>(state = State.Ready, data = data)
    class Loading<T>: StateData<T>(state = State.Loading)
    class Error<T>(message: String): StateData<T>(state = State.Error, message = message)

    enum class State {
        Inactive,
        Loading,
        Ready,
        Error
    }
}