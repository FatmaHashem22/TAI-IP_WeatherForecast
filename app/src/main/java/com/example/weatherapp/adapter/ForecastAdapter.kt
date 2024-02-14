package com.example.weatherapp.adapter

import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.weatherapp.R
import com.example.weatherapp.model.WeatherListItem
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ForecastAdapter(
    var forecastList : List<WeatherListItem?>
) : RecyclerView.Adapter<ForecastAdapter.ForecastViewHolder>(){




    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastViewHolder {
        val inflate = LayoutInflater.from(parent.context)
        val view = inflate.inflate(R.layout.upcoming_forecast_list,parent,false)
        return ForecastViewHolder(view)
    }

    override fun getItemCount(): Int = forecastList.size

    override fun onBindViewHolder(holder: ForecastViewHolder, position: Int) {
        val item = forecastList.get(position)

        val day = getDayFromDt(item?.dt!!)
        holder.dayText.text = day

        holder.humidity.text = item?.main!!.humidity.toString()+"%"
        holder.wind.text = item?.wind!!.speed.toString()+" m/s"


        val tempFerhn = item?.main?.temp
        val tempCels = tempFerhn?.minus(273.15)?.toInt()
        val tempFormatted = tempCels.toString()

        holder.dayTemp.text = "$tempFormatted°C"

        val rainPercent = (item?.pop!! * 100).toInt()

        holder.chanceOfRain.text = "$rainPercent%"

        for ( i in item?.weather!!) {

            // Clear Sky Day
            if(i?.icon == "01d") {
                holder.statusIcon.setImageResource(R.drawable.ic_sunny)
            }

            // Clear Sky Night
            if(i?.icon == "01n") {
                holder.statusIcon.setImageResource(R.drawable.moon_stars)
            }

            // Partly Cloudy Day
            if(i?.icon == "02d") {
                holder.statusIcon.setImageResource(R.drawable.ic_sunnycloudy)
            }

            // Partly Cloudy Night
            if(i?.icon == "02n") {
                holder.statusIcon.setImageResource(R.drawable.moon_clouds)
            }

            // Cloudy
            if (i?.icon == "04d" || i?.icon == "04n") {
                holder.statusIcon.setImageResource(R.drawable.ic_cloudy)
            }

            // Light Rain Day and Night
            if(i?.icon == "09d" || i?.icon == "09n") {
                holder.statusIcon.setImageResource(R.drawable.ic_rainy)
            }

            // Rain Day and Night
            if(i?.icon == "10d" || i?.icon == "10n") {
                holder.statusIcon.setImageResource(R.drawable.ic_rainshower)
            }

            // Thunder Day and Night
            if(i?.icon == "11d" || i?.icon == "11n") {
                holder.statusIcon.setImageResource(R.drawable.ic_thunder)
            }

            // Snow Day and Night
            if(i?.icon == "13d" || i?.icon == "13n") {
                holder.statusIcon.setImageResource(R.drawable.ic_heavysnow)
            }

            // Mist Day and Night
            if(i?.icon == "50d" || i?.icon == "50n") {
                holder.statusIcon.setImageResource(R.drawable.mist)
            }

        }




    }

    private fun getDayFromDt(dt: Long): String {

        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val date = Date(dt * 1000L)
        val outputFormat = SimpleDateFormat("EEE", Locale.getDefault())

        return outputFormat.format(date!!).uppercase()

    }

    fun updateForecast(newList : List<WeatherListItem?>){
        forecastList = newList
        notifyDataSetChanged()
    }

    class ForecastViewHolder(itemView : View) : ViewHolder(itemView) {

        val dayText : TextView = itemView.findViewById(R.id.day_of_week)
        val statusIcon : ImageView = itemView.findViewById(R.id.forecast_weather_icon)
        val dayTemp : TextView = itemView.findViewById(R.id.day_temp)
        val chanceOfRain : TextView = itemView.findViewById(R.id.chance_of_rain_forecast)
        val wind : TextView = itemView.findViewById(R.id.wind_speed_forecast)
        val humidity : TextView = itemView.findViewById(R.id.humidity_forecast)
    }
}