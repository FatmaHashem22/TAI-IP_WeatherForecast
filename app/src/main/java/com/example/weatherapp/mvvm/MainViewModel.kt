package com.example.weatherapp.mvvm

import androidx.lifecycle.MutableLiveData

import androidx.lifecycle.ViewModel
import com.example.weatherapp.api.APIManager
import com.example.weatherapp.model.ForecastResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class MainViewModel : ViewModel() {

    private val _apiResult = MutableStateFlow<ApiResult<ForecastResponse>?>(null)


    val apiResult: Flow<ApiResult<ForecastResponse>?> = _apiResult.asStateFlow()


    private val apiScope = CoroutineScope(Dispatchers.IO)


    fun fetchData(lat: String, lng: String) {

        apiScope.launch {

            _apiResult.value = ApiResult.Loading

            try {

                val response = APIManager.getAPIs().getWeatherData(lat, lng).execute()

                if (response.isSuccessful) {

                    _apiResult.value = ApiResult.Success(response.body()!!)

                } else {

                    _apiResult.value = ApiResult.Error(RuntimeException("API request failed with code ${response.code()}"))

                }

            } catch (e: Exception) {

                _apiResult.value = ApiResult.Error(e)

            }

        }

    }

}

sealed class ApiResult<out T> {

    data class Success<out T>(val data: T) : ApiResult<T>()

    data class Error(val exception: Exception) : ApiResult<Nothing>()

    object Loading : ApiResult<Nothing>()

}