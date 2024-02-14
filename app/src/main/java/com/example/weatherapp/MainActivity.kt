package com.example.weatherapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.icu.text.SimpleDateFormat
import android.location.Location
import android.os.Bundle
import com.example.weatherapp.baseActivity.BaseActivity
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.SearchView.OnQueryTextListener
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.adapter.ForecastAdapter
import com.example.weatherapp.adapter.TodaysForecastAdapter
import com.example.weatherapp.api.APIManager
import com.example.weatherapp.model.ForecastResponse
import com.example.weatherapp.model.WeatherListItem
import com.example.weatherapp.mvvm.ApiResult
import com.example.weatherapp.mvvm.MainViewModel
import com.google.android.gms.location.CurrentLocationRequest
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Date
import java.util.Locale

class MainActivity : BaseActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var viewModel: MainViewModel

    lateinit var todayRecyclerView: RecyclerView
    lateinit var todayAdapter: TodaysForecastAdapter
    lateinit var forecastRecyclerView: RecyclerView
    lateinit var forecastAdapter: ForecastAdapter

    lateinit var cityName: TextView
    lateinit var searchView: SearchView

    lateinit var statusImage: ImageView
    lateinit var currentTemp: TextView
    lateinit var currentDesc: TextView
    lateinit var currDay: TextView
    lateinit var currPOP: TextView
    lateinit var currWind: TextView
    lateinit var currHumidity: TextView

    lateinit var progressBar : ProgressBar
    lateinit var mainLayout : LinearLayout


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (isGPAPersmissionAllowed()) {
            initViews()
            getUserLocation()

            initListeners()

        } else {
            requestPersmissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }



    }
    val requestPersmissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                // customer agrees so we are able too access the location
                getUserLocation()
            } else {
                // customer didn't agree
                // explain why we need to access the location
                // Ask the customer for permission again

                showDialog(
                    this,
                    message = "Please Enable Location Permission to see location's forecast.",
                    posActionTitle = "Accept",
                    posAction = { dialog, which ->
                        dialog.dismiss()
                    },
                    negActionTitle = "Deny",
                    negAction = { dialog, which ->
                        dialog.dismiss()
                    }
                )

            }
        }

    fun isGPAPersmissionAllowed(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    var lat: String = ""
    var lng: String = ""


    @SuppressLint("MissingPermission")
    fun getUserLocation() {

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val currentLocationBuilder = CurrentLocationRequest.Builder()
        currentLocationBuilder.setPriority(Priority.PRIORITY_HIGH_ACCURACY)
        val currentLocationRequest = currentLocationBuilder.build()
        fusedLocationClient.getCurrentLocation(currentLocationRequest, null)
            .addOnSuccessListener { location: Location ->
                lat = location.latitude.toString()
                lng = location.longitude.toString()

                Log.e("LAT= ", "$lat")
                Log.e("LNG= ", "$lng")

                viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

                viewModel.apiResult.onEach { apiResult ->

                    when (apiResult) {

                        is ApiResult.Loading -> {

                            // show loading indicator

                        }

                        is ApiResult.Success -> {

                            // update UI with the API response

                            val response = apiResult.data
                            val forecastList = response?.list!!
                            val uniqueForecastList = mutableListOf<WeatherListItem>()

                            Log.e("SIZE:","${forecastList.size}")
                            for (item in forecastList.indices) {
                                Log.e("ITERATORRR:","$item")


                                if (item == 0 || item == 7 || item == 14 || item == 21 || item == 28) {

                                    uniqueForecastList.add(forecastList[item]!!)

                                }

                            }

                            currentDataForecast(response?.list!!)

                            todayAdapter.updateDay(response?.list!!)

                            forecastAdapter.updateForecast(uniqueForecastList)

                            progressBar.isVisible = false
                            mainLayout.isVisible = true

                        }

                        is ApiResult.Error -> {

                            Toast.makeText(

                                this,

                                "Something went wrong, please try again later!",

                                Toast.LENGTH_LONG

                            )

                        }

                        else -> {}
                    }

                }.launchIn(lifecycleScope)

                viewModel.fetchData(lat, lng)


//                currentForecast(lat,lng)




            }
    }

    fun initViews() {

        mainLayout = findViewById(R.id.main_page)
        progressBar = findViewById(R.id.loader)
        todayRecyclerView = findViewById(R.id.main_recycler_view)
        todayAdapter = TodaysForecastAdapter(listOf())
        todayRecyclerView.adapter = todayAdapter

        forecastRecyclerView = findViewById(R.id.forecast_recycler_view)
        forecastAdapter = ForecastAdapter(listOf())
        forecastRecyclerView.adapter = forecastAdapter

        cityName = findViewById(R.id.cityName)

        searchView = findViewById(R.id.searchView)


        statusImage = findViewById(R.id.image_main)
        currentTemp = findViewById(R.id.current_temp)
        currentDesc = findViewById(R.id.weather_status)
        currDay = findViewById(R.id.date_day_main)
        currPOP = findViewById(R.id.chance_of_rain)
        currWind = findViewById(R.id.wind_speed)
        currHumidity = findViewById(R.id.humidity)

        progressBar.isVisible = true
        mainLayout.isVisible = false

    }

    fun initListeners() {
        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {

                if (query != null && query.isNotEmpty()) {
                    forecastByCity(query)

                    cityName.text = query

                    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(window.decorView.windowToken, 0)

                    searchView.setQuery("", false)
                    searchView.clearFocus()
                    searchView.isSubmitButtonEnabled = true
                }


                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                return false
            }

        })
    }

    fun forecastByCity(city: String) {

        if (city != null) {
            APIManager
                .getAPIs()
                .getWeatherByCity(
                    city
                ).enqueue(object : Callback<ForecastResponse> {
                    override fun onResponse(
                        call: Call<ForecastResponse>,
                        response: Response<ForecastResponse>
                    ) {
                        if(response.body() != null){
                            Log.e("OnResponse", "${response.body()?.list}")

                            val forecastList = response.body()?.list!!
                            val uniqueForecastList = mutableListOf<WeatherListItem>()

                            for (item in forecastList.indices) {
                                Log.e("ITERATORRR:","$item")


                                if (item == 0 || item == 7 || item == 14 || item == 21 || item == 28) {

                                    uniqueForecastList.add(forecastList[item]!!)

                                }

                            }
                            runOnUiThread {
                                currentDataForecast(response?.body()?.list!!)
                                todayAdapter.updateDay(response?.body()?.list!!)
                                forecastAdapter.updateForecast(uniqueForecastList!!)
                                }
                        }
                    }

                    override fun onFailure(call: Call<ForecastResponse>, t: Throwable) {
                        Log.e("OnFailure", "$t")
                        Toast.makeText(
                            this@MainActivity,
                            "Something went wrong, please try again later!",
                            Toast.LENGTH_LONG
                        )
                    }

                })
        }



    }

    fun currentDataForecast(forecastList : List<WeatherListItem?>) {


        var icon = forecastList.get(0)?.weather?.get(0)?.icon

        var rain = (forecastList.get(0)?.pop!! * 100).toInt()
        currPOP.text = "$rain%"

        var wind = forecastList.get(0)?.wind?.speed.toString()
        currWind.text = "$wind m/s"

        var humidity = forecastList.get(0)?.main?.humidity.toString()
        currHumidity.text = "$humidity%"

        var temp = forecastList.get(0)?.main?.temp
        val tempCels = temp?.minus(273.15)?.toInt()
        val tempFormatted = tempCels.toString()
        currentTemp.text = "$tempFormatted°C"

        var desc = forecastList.get(0)?.weather?.get(0)?.description
        currentDesc.text = desc

        var day = forecastList.get(0)?.dt
        val date = Date(day!! * 1000L)
        val outputFormat = SimpleDateFormat("EEEE d", Locale.getDefault()).format(date)
        currDay.text = outputFormat


        if(icon == "01d") {
            statusImage.setImageResource(R.drawable.ic_sunny)
        }

        // Clear Sky Night
        if(icon == "01n") {
            statusImage.setImageResource(R.drawable.moon_stars)
        }

        // Partly Cloudy Day
        if(icon == "02d") {
            statusImage.setImageResource(R.drawable.ic_sunnycloudy)
        }

        // Partly Cloudy Night
        if(icon == "02n") {
            statusImage.setImageResource(R.drawable.moon_clouds)
        }

        // Cloudy
        if (icon == "04d" || icon == "04n") {
            statusImage.setImageResource(R.drawable.ic_cloudy)
        }

        // Light Rain Day and Night
        if(icon == "09d" || icon == "09n") {
            statusImage.setImageResource(R.drawable.ic_rainy)
        }

        // Rain Day and Night
        if(icon == "10d" || icon == "10n") {
            statusImage.setImageResource(R.drawable.ic_rainshower)
        }

        // Thunder Day and Night
        if(icon == "11d" || icon == "11n") {
            statusImage.setImageResource(R.drawable.ic_thunder)
        }

        // Snow Day and Night
        if(icon == "13d" || icon == "13n") {
            statusImage.setImageResource(R.drawable.ic_heavysnow)
        }

        // Mist Day and Night
        if(icon == "50d" || icon == "50n") {
            statusImage.setImageResource(R.drawable.mist)
        }

    }




}