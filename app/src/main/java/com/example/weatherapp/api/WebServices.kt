package com.example.weatherapp.api


import com.example.weatherapp.Constants
import com.example.weatherapp.model.ForecastResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


interface WebServices {

    @GET("forecast/")
    fun getWeatherData(
        @Query("lat") lat : String,
        @Query("lon") lng : String,
        @Query("appid") apiKey: String = Constants.API_KEY
    ) : Call<ForecastResponse>

    @GET("forecast/")
    fun getWeatherByCity(
        @Query("q") city : String,
        @Query("appid") apiKey: String = Constants.API_KEY
    ) : Call<ForecastResponse>

}