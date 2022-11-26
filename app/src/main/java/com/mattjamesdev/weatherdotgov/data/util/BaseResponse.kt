package com.mattjamesdev.weatherdotgov.data.util

import retrofit2.Response

abstract class BaseApiResponse {
    suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): NetworkResult<T> {
        try {
            val response = apiCall()
            if (response.isSuccessful) {
                val body = response.body()
                body?.let {
                    return NetworkResult.Success(data = body) // needs to be wrapped in Data<T>
                }
            }
            return error("${response.code()}: ${response.message()}")
        } catch (e: Exception) {
            return error(e.message ?: e.toString())
        }
    }

    private fun <T> error(errorMessage: String) : NetworkResult<T> =
        NetworkResult.Error(message = "Api request failed: $errorMessage")
}