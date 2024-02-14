package com.example.weatherapp.api

import com.example.weatherapp.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class APIManager {
    companion object {
        private var retrofit : Retrofit? = null

        private fun getInstance() : Retrofit {

            if(retrofit == null) {

                val logging = HttpLoggingInterceptor()
                logging.setLevel(HttpLoggingInterceptor.Level.BODY)
                val client = OkHttpClient.Builder()
                    .addInterceptor(logging)
                    .build()
                retrofit = Retrofit
                    .Builder()
                    .client(client)
                    .baseUrl(Constants.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }

            return retrofit!!
        }

        fun getAPIs() : WebServices {
            return getInstance().create(WebServices::class.java)
        }

    }
}